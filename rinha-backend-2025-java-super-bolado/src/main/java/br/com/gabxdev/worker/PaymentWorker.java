package br.com.gabxdev.worker;

import br.com.gabxdev.client.PaymentProcessorClient;
import br.com.gabxdev.model.Payment;
import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;
import br.com.gabxdev.repository.InMemoryPaymentDatabase;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public final class PaymentWorker {

    private final static PaymentWorker INSTANCE = new PaymentWorker();

    private final Integer workerPoolSize;

    private final ConcurrentLinkedQueue<Payment> pendingPayments = new ConcurrentLinkedQueue<>();

    private final InMemoryPaymentDatabase paymentRepository = InMemoryPaymentDatabase.getInstance();

    private final PaymentProcessorClient processorClient = PaymentProcessorClient.getInstance();

    private final Semaphore semaphore = new Semaphore(0);

    private PaymentWorker() {
        var applicationProperties = ApplicationProperties.getInstance();

        var workerPoolSizeS = applicationProperties.getProperty(PropertiesKey.WORKER_POOL_SIZE);

        this.workerPoolSize = Integer.parseInt(workerPoolSizeS);

        Thread.startVirtualThread(this::start);
    }

    public static PaymentWorker getInstance() {
        return INSTANCE;
    }

    private void start() {
        for (int i = 0; i < workerPoolSize; i++) {
            Thread.startVirtualThread(this::runWorker);
        }
    }

    private void runWorker() {
        while (true) {
            try {
                semaphore.acquire();

                var request = pendingPayments.poll();

                processPayment(request);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void enqueue(Payment request) {
        pendingPayments.offer(request);

        semaphore.release();
    }

    private void processPayment(Payment payment) {
        if (processorClient.sendPayment(payment)) {
            paymentRepository.save(payment);

            return;
        }

        enqueue(payment);
    }
}
