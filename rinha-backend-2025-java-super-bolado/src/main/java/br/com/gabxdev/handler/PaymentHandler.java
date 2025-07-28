package br.com.gabxdev.handler;

import br.com.gabxdev.model.Payment;
import br.com.gabxdev.service.PaymentService;
import br.com.gabxdev.worker.PaymentWorker;

import java.net.InetAddress;

public final class PaymentHandler {

    private static final PaymentHandler INSTANCE = new PaymentHandler();

    private final PaymentWorker paymentWorker = PaymentWorker.getInstance();

    private final PaymentService paymentService = PaymentService.getInstance();

    private PaymentHandler() {
    }

    public static PaymentHandler getInstance() {
        return INSTANCE;
    }

    public void receivePayment(Payment payment) {
        paymentWorker.enqueue(payment);
    }

    public void purgePayments() {
        paymentService.purgePayments();
    }

    public void paymentSummary(String payload, InetAddress addressLb, int portLb) {
        paymentService.getPaymentSummary(payload, addressLb, portLb);
    }
}
