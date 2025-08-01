package br.com.gabxdev.lb;

import java.util.concurrent.ArrayBlockingQueue;

public final class PaymentSummaryWaiter {

    private final static PaymentSummaryWaiter INSTANCE = new PaymentSummaryWaiter();

    private final ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(1);

    private PaymentSummaryWaiter() {
    }

    public static PaymentSummaryWaiter getInstance() {
        return INSTANCE;
    }

    public String awaitResponse() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void completeResponse(String response) {
        queue.offer(response);
    }
}
