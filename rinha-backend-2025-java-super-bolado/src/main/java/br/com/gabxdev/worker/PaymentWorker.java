package br.com.gabxdev.worker;

import br.com.gabxdev.client.PaymentProcessorClient;
import br.com.gabxdev.model.Payment;
import br.com.gabxdev.repository.InMemoryPaymentDatabase;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

@Component
public class PaymentWorker {

    @Value("${rinha.server.http-client-worker}")
    private Integer workersQuantity;

    @Value("${rinha.queue-buffer}")
    private Integer queueBuffer;

    private LinkedBlockingQueue<Payment> queue;

    private final InMemoryPaymentDatabase paymentRepository;

    private final PaymentProcessorClient processorClient;

    public PaymentWorker(InMemoryPaymentDatabase paymentRepository, PaymentProcessorClient processorClient) {
        this.paymentRepository = paymentRepository;
        this.processorClient = processorClient;
    }

    @PostConstruct
    public void start() {
        this.queue = new LinkedBlockingQueue<>(queueBuffer);

        for (int i = 0; i < workersQuantity; i++) {
            Thread.startVirtualThread(this::runWorker);
        }
    }

    private void runWorker() {
        while (true) {
            var request = takePayment();

            processPayment(request);
        }
    }

    public Payment takePayment() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void enqueue(Payment request) {
        if (!queue.offer(request)) {
            throw new RuntimeException("queue full");
        }
    }

    private void processPayment(Payment payment) {
        var isSuccessful = processorClient.sendPayment(payment);

        if (isSuccessful) {
            paymentRepository.save(payment);

            return;
        }

        enqueue(payment);
    }
}
