package br.com.gabxdev.service;

import br.com.gabxdev.client.UdpClient;
import br.com.gabxdev.config.ApiSockerInternalConfig;
import br.com.gabxdev.config.LoudBalanceChannelConfig;
import br.com.gabxdev.config.UnixSocketConfig;
import br.com.gabxdev.mapper.JsonParse;
import br.com.gabxdev.middleware.PaymentMiddleware;
import br.com.gabxdev.middleware.PaymentSummaryWaiter;
import br.com.gabxdev.model.Event;
import br.com.gabxdev.model.enums.EventType;
import br.com.gabxdev.repository.InMemoryPaymentDatabase;
import br.com.gabxdev.response.PaymentSummaryGetResponse;
import org.newsclub.net.unix.AFUNIXDatagramSocket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

import static br.com.gabxdev.mapper.JsonParse.parseInstant;

public final class PaymentService {

    private static final PaymentService INSTANCE = new PaymentService();

    private static final long YEAR_2000 = 946684800000L;

    private final UdpClient udpClient = UdpClient.getInstance();

    private final DatagramSocket datagramSocketExternal = ApiSockerInternalConfig.getInstance().getDatagramSocket();

    private final InMemoryPaymentDatabase paymentRepository = InMemoryPaymentDatabase.getInstance();

    private final PaymentMiddleware paymentMiddleware = PaymentMiddleware.getInstance();

    private final PaymentSummaryWaiter paymentSummaryWaiter = PaymentSummaryWaiter.getInstance();

    private final DatagramSocket lbSocket = LoudBalanceChannelConfig.getInstance().getSocket();

    private PaymentService() {
    }

    public static PaymentService getInstance() {
        return INSTANCE;
    }

    public void paymentSummaryToMerge(String eventJson) {
        var instants = eventJson.split("@");
        var from = parseInstant(instants[0]);
        var to = parseInstant(instants[1]);

        var paymentSummary = getSummary(from, to);

        var payload = Event.buildEventDTO(EventType.PAYMENT_SUMMARY_MERGE.ordinal(),
                JsonParse.parseToJsonPaymentSummaryInternal(paymentSummary)).getBytes(StandardCharsets.UTF_8);

        udpClient.send(new DatagramPacket(payload, payload.length), datagramSocketExternal);
    }

    public void getPaymentSummary(String payload) {
        var instants = payload.split("@");
        var from = parseInstant(instants[0]);
        var to = parseInstant(instants[1]);

        paymentMiddleware.syncPaymentSummary(instants[0], instants[1]);

        var paymentSummary2 = getSummary(from, to);
        var paymentSummary1 = paymentSummaryWaiter.awaitResponse();

        var paymentSummaryMerged = PaymentMiddleware.mergeSummary(paymentSummary1, paymentSummary2);
        var response = JsonParse.parseToJsonPaymentSummary(paymentSummaryMerged).getBytes(StandardCharsets.UTF_8);

        udpClient.send(new DatagramPacket(response, response.length), lbSocket);
    }

    private PaymentSummaryGetResponse getSummary(long from, long to) {
        if (from == YEAR_2000) {
            return paymentRepository.getTotalSummary();
        } else {
            return paymentRepository.getSummaryByTimeRange(from, to);
        }
    }

    public void purgePayments() {
        paymentRepository.deleteAll();

        paymentMiddleware.purgePayments();
    }

    public void purgePaymentsInternal() {
        paymentRepository.deleteAll();
    }
}
