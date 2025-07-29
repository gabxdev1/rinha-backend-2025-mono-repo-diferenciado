package br.com.gabxdev.service;

import br.com.gabxdev.client.UdpClient;
import br.com.gabxdev.config.SocketInternalConfig;
import br.com.gabxdev.mapper.JsonParse;
import br.com.gabxdev.middleware.PaymentMiddleware;
import br.com.gabxdev.middleware.PaymentSummaryWaiter;
import br.com.gabxdev.model.Event;
import br.com.gabxdev.model.enums.EventType;
import br.com.gabxdev.repository.InMemoryPaymentDatabase;
import br.com.gabxdev.response.PaymentSummaryGetResponse;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;

import static br.com.gabxdev.mapper.JsonParse.parseInstant;

public final class PaymentService {

    private static final PaymentService INSTANCE = new PaymentService();

    private final UdpClient udpClient = UdpClient.getInstance();

    private final DatagramSocket datagramSocketExternal = SocketInternalConfig.getInstance().getDatagramSocket();

    private final InMemoryPaymentDatabase paymentRepository = InMemoryPaymentDatabase.getInstance();

    private final PaymentMiddleware paymentMiddleware = PaymentMiddleware.getInstance();

    private final PaymentSummaryWaiter paymentSummaryWaiter = PaymentSummaryWaiter.getInstance();

    private PaymentService() {
    }

    public static PaymentService getInstance() {
        return INSTANCE;
    }

    public void paymentSummaryToMerge(String eventJson) {
        var instants = eventJson.split("@");
        var from = parseInstant(instants[0]);
        var to = parseInstant(instants[1]);

        PaymentSummaryGetResponse paymentSummary;

        if (from.atZone(ZoneOffset.UTC).getYear() == 2000) {
            paymentSummary = paymentRepository.getTotalSummary();
        } else {
            paymentSummary = paymentRepository.getSummaryByTimeRange(from, to);
        }

        var payload = Event.buildEventDTO(EventType.PAYMENT_SUMMARY_MERGE.ordinal(),
                JsonParse.parseToJsonPaymentSummaryInternal(paymentSummary)).getBytes(StandardCharsets.UTF_8);

        sendSummary(datagramSocketExternal, new DatagramPacket(payload, payload.length));
    }

    public String getPaymentSummary(String fromS, String toS) {
        var from = parseInstant(fromS);
        var to = parseInstant(toS);

        paymentMiddleware.syncPaymentSummary(from.toString(), to.toString());

        var paymentSummary2 = internalGetPaymentSummary(from, to);
        var paymentSummary1 = paymentSummaryWaiter.awaitResponse();

        var paymentSummaryMerged = PaymentMiddleware.mergeSummary(paymentSummary1, paymentSummary2);

        return JsonParse.parseToJsonPaymentSummary(paymentSummaryMerged);
    }

    private PaymentSummaryGetResponse internalGetPaymentSummary(Instant from, Instant to) {
        if (from.atZone(ZoneOffset.UTC).getYear() == 2000) {
            return paymentRepository.getTotalSummary();
        } else {
            return paymentRepository.getSummaryByTimeRange(from, to);
        }
    }

    private void sendSummary(DatagramSocket datagramSocket, DatagramPacket datagramPacket) {
        udpClient.send(datagramPacket, datagramSocket);
    }

    public void purgePayments() {

        paymentRepository.deleteAll();

        paymentMiddleware.purgePayments();
    }

    public void purgePaymentsInternal() {
        paymentRepository.deleteAll();
    }
}
