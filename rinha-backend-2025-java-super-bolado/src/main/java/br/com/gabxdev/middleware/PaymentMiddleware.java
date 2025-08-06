package br.com.gabxdev.middleware;

import br.com.gabxdev.client.UdpClient;
import br.com.gabxdev.config.ApiSockerInternalConfig;
import br.com.gabxdev.mapper.EventMapper;
import br.com.gabxdev.response.PaymentSummaryGetResponse;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PaymentMiddleware {

    private final static PaymentMiddleware INSTANCE = new PaymentMiddleware();

    private final DatagramSocket datagramSocketExternal = ApiSockerInternalConfig.getInstance().getDatagramSocket();

    private final ExecutorService pool = Executors.newFixedThreadPool(1, Thread.ofVirtual().factory());

    private final UdpClient udpClient = UdpClient.getInstance();

    private PaymentMiddleware() {
    }

    public static PaymentMiddleware getInstance() {
        return INSTANCE;
    }

    public void purgePayments() {
        callBackEndToPurgePayments();
    }

    public void syncPaymentSummary(String from, String to) {
        CompletableFuture.runAsync(() -> {
            callBackEndSummary(from, to);
        }, pool);
    }

    public static PaymentSummaryGetResponse mergeSummary(PaymentSummaryGetResponse summary1,
                                                         PaymentSummaryGetResponse summary2) {
        var api1TotalAmount1 = summary1.getDefaultApi().getTotalAmount();
        var api2TotalAmount1 = summary2.getDefaultApi().getTotalAmount();

        var api1TotalAmount2 = summary1.getFallbackApi().getTotalAmount();
        var api2TotalAmount2 = summary2.getFallbackApi().getTotalAmount();


        var api1TotalRequests1 = summary1.getDefaultApi().getTotalRequests();
        var api2TotalRequests1 = summary2.getDefaultApi().getTotalRequests();

        var api1TotalRequests2 = summary1.getFallbackApi().getTotalRequests();
        var api2TotalRequests2 = summary2.getFallbackApi().getTotalRequests();


        summary1.getDefaultApi().setTotalAmount(api1TotalAmount1.add(api2TotalAmount1));
        summary1.getFallbackApi().setTotalAmount(api1TotalAmount2.add(api2TotalAmount2));


        summary1.getDefaultApi().setTotalRequests(api1TotalRequests1 + api2TotalRequests1);
        summary1.getFallbackApi().setTotalRequests(api1TotalRequests2 + api2TotalRequests2);

        return summary1;
    }

    private void callBackEndToPurgePayments() {
        var request = EventMapper.toPurgePaymentsPostRequest();

        sendEvent(request.getBytes(StandardCharsets.UTF_8));
    }

    private void callBackEndSummary(String from, String to) {
        var request = EventMapper.toPaymentSummaryGetRequest(from, to).getBytes(StandardCharsets.UTF_8);

        sendEvent(request);
    }

    private void sendEvent(byte[] request) {
        udpClient.send(new DatagramPacket(request, request.length), datagramSocketExternal);
    }
}
