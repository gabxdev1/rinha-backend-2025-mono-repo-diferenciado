package br.com.gabxdev.router;

import br.com.gabxdev.config.DatagramSocketConfig;
import br.com.gabxdev.handler.PaymentHandler;
import br.com.gabxdev.mapper.PaymentMapper;
import br.com.gabxdev.model.enums.EventType;
import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public final class PaymentRouter {

    private static final PaymentRouter INSTANCE = new PaymentRouter();

    private final PaymentHandler paymentHandler = PaymentHandler.getInstance();


    private PaymentRouter() {
        start();
    }

    public static PaymentRouter getInstance() {
        return INSTANCE;
    }

    private void start() {
        Thread.startVirtualThread(() -> {
            try {
                handleEvents();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleEvents() throws IOException {
        var poolSize = ApplicationProperties.getInstance().getProperty(PropertiesKey.HANDLER_UDP_POOL_SIZE);
        var poll = Executors.newFixedThreadPool(Integer.parseInt(poolSize), Thread.ofVirtual().factory());
        var datagramSocket = DatagramSocketConfig.getInstance().getDatagramSocket();

        while (true) {
            var buffer = new byte[200];

            var datagramPacket = new DatagramPacket(buffer, buffer.length);

            datagramSocket.receive(datagramPacket);

            CompletableFuture.runAsync(() -> {
                mapperEvent(datagramPacket.getData(), datagramPacket.getAddress(), datagramPacket.getPort());
            }, poll);
        }
    }

    private void mapperEvent(byte[] data, InetAddress addressLb, int portLb) {
        routerEvent(new String(data, StandardCharsets.UTF_8).trim(), addressLb,  portLb);
    }

    private void routerEvent(String event, InetAddress addressLb, int portLb) {
        var key = event.toCharArray()[event.length() - 1];

        if (EventType.valueOf(key).equals(EventType.PAYMENT_POST)) {
            paymentHandler.receivePayment(PaymentMapper.toPayment(event));
        }

        if (EventType.valueOf(key).equals(EventType.PAYMENT_SUMMARY)) {
            paymentHandler.paymentSummary(event.replace("a", ""), addressLb, portLb);
        }

        if (EventType.valueOf(key).equals(EventType.PURGE)) {
            paymentHandler.purgePayments();
        }
    }
}
