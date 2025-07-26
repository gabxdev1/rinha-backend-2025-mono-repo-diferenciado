package br.com.gabxdev.worker;

import br.com.gabxdev.client.PaymentProcessorClient;
import br.com.gabxdev.model.Payment;
import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;
import br.com.gabxdev.repository.InMemoryPaymentDatabase;

import java.util.concurrent.ArrayBlockingQueue;

public final class PaymentWorker {

    private final static PaymentWorker INSTANCE = new PaymentWorker();

    private final Integer workerPoolSize;

    private final ArrayBlockingQueue<Payment> queue;

    private final InMemoryPaymentDatabase paymentRepository = InMemoryPaymentDatabase.getInstance();

    private final PaymentProcessorClient processorClient = PaymentProcessorClient.getInstance();

    private PaymentWorker() {
        var applicationProperties = ApplicationProperties.getInstance();

        var queueBufferS = applicationProperties.getProperty(PropertiesKey.QUEUE_BUFFER);
        var workerPoolSizeS = applicationProperties.getProperty(PropertiesKey.WORKER_POOL_SIZE);

        this.queue = new ArrayBlockingQueue<>(Integer.parseInt(queueBufferS));
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
            var request = takePayment();

            processPayment(request);
        }
    }

    private Payment takePayment() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void enqueue(Payment request) {
        queue.offer(request);
    }

    private void processPayment(Payment payment) {
        if (processorClient.sendPayment(payment)) {
            paymentRepository.save(payment);

            return;
        }

        enqueue(payment);
    }
}
