package br.com.gabxdev.router;

import br.com.gabxdev.config.UnixSocketConfig;
import br.com.gabxdev.handler.PaymentHandler;
import br.com.gabxdev.mapper.PaymentMapper;
import br.com.gabxdev.model.enums.EventType;
import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public final class PaymentRouter {

    private static final PaymentRouter INSTANCE = new PaymentRouter();

    private final PaymentHandler paymentHandler = PaymentHandler.getInstance();

    private final ConcurrentLinkedQueue<byte[]> packets = new ConcurrentLinkedQueue<>();

    private final Semaphore semaphore = new Semaphore(0);

    private PaymentRouter() {
        Thread.startVirtualThread(() -> {
            try {
                handleEvents();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        var poolSize = ApplicationProperties.getInstance().getProperty(PropertiesKey.HANDLER_UDP_POOL_SIZE);

        for (var i = 0; i < Integer.parseInt(poolSize); i++) {
            Thread.startVirtualThread(this::processEvent);
        }
    }

    public static PaymentRouter getInstance() {
        return INSTANCE;
    }


    private void handleEvents() throws IOException {
        var socket = UnixSocketConfig.getInstance().getSocket();

        while (true) {

            var buffer = new byte[120];

            var packet = new DatagramPacket(buffer, buffer.length);

            socket.receive(packet);

            packets.offer(packet.getData());

            semaphore.release();
        }
    }

    public void processEvent() {
        while (true) {
            try {
                semaphore.acquire();

                var packet = packets.poll();

                routerEvent(new String(packet, StandardCharsets.UTF_8));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void routerEvent(String event) {
        var key = event.charAt(event.length() - 1);

        if (EventType.valueOf(key).equals(EventType.PAYMENT_SUMMARY)) {
            paymentHandler.paymentSummary(event.replace("a", ""));

            return;
        }

        if (EventType.valueOf(key).equals(EventType.PURGE)) {
            paymentHandler.purgePayments();

            return;
        }

        paymentHandler.receivePayment(PaymentMapper.toPayment(event));
    }
}
