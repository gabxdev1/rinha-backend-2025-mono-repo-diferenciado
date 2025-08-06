package br.com.gabxdev.consumer;

import br.com.gabxdev.config.UnixSocketConfig;
import br.com.gabxdev.mapper.PaymentMapper;
import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;
import br.com.gabxdev.worker.PaymentWorker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class PaymentPostConsumer {
    private final static PaymentPostConsumer INSTANCE = new PaymentPostConsumer();

    private final ConcurrentLinkedQueue<String> packets = new ConcurrentLinkedQueue<>();

    private final Semaphore semaphore = new Semaphore(0);

    private PaymentPostConsumer() {
        Thread.startVirtualThread(() -> {
            try {
                packetsHandler();
            } catch (Exception e) {
                System.out.println("Error handler consumer payment post: " + e.getMessage());
            }
        });

        var poolSize = ApplicationProperties.getInstance().getProperty(PropertiesKey.HANDLER_UDP_POOL_SIZE);

        for (var i = 0; i < Integer.parseInt(poolSize); i++) {
            Thread.startVirtualThread(() -> {
                try {
                    processPaymentPost();
                } catch (InterruptedException e) {
                    System.out.println("Error processPaymentPost: " + e.getMessage());
                }
            });
        }
    }

    public static PaymentPostConsumer getInstance() {
        return INSTANCE;
    }

    private void packetsHandler() throws IOException {
        var socket = UnixSocketConfig.getInstance().getSocket();

        var buffer = new byte[140];

        var packet = new DatagramPacket(buffer, buffer.length);

        while (true) {
            socket.receive(packet);

            packets.offer(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));

            semaphore.release();
        }
    }

    private void processPaymentPost() throws InterruptedException {
        var paymentWorker = PaymentWorker.getInstance();

        while (true) {
            semaphore.acquire();

            var packet = packets.poll();

            paymentWorker.enqueue(PaymentMapper.toPayment(packet));
        }
    }
}
