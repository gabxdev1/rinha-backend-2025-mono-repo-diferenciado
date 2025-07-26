package br.com.gabxdev.middleware;

import br.com.gabxdev.mapper.PaymentSummaryMapper;
import br.com.gabxdev.response.PaymentSummaryGetResponse;

import java.util.concurrent.ArrayBlockingQueue;

public final class PaymentSummaryWaiter {

    private final static PaymentSummaryWaiter INSTANCE = new PaymentSummaryWaiter();

    private final ArrayBlockingQueue<PaymentSummaryGetResponse> queue = new ArrayBlockingQueue<>(1);

    private PaymentSummaryWaiter() {
    }

    public static PaymentSummaryWaiter getInstance() {
        return INSTANCE;
    }

    public PaymentSummaryGetResponse awaitResponse() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void completeResponse(String response) {
        var paymentSummary = PaymentSummaryMapper.toPaymentSummary(response);

        queue.offer(paymentSummary);
    }
}
