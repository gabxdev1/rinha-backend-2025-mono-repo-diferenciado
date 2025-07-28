package br.com.gabxdev.router;

import br.com.gabxdev.config.DatagramSocketConfig;
import br.com.gabxdev.handler.PaymentHandler;
import br.com.gabxdev.mapper.PaymentMapper;
import br.com.gabxdev.model.Event;
import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public final class PaymentRouter {

    private static final PaymentRouter INSTANCE = new PaymentRouter();

    private final PaymentHandler paymentHandler = PaymentHandler.getInstance();

    private final DatagramSocket datagramSocket = DatagramSocketConfig.getInstance().getDatagramSocket();


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

        while (true) {
            var buffer = new byte[60];

            var datagramPacket = new DatagramPacket(buffer, buffer.length);

            datagramSocket.receive(datagramPacket);

            CompletableFuture.runAsync(() -> {
                mapperEvent(datagramPacket.getData(), datagramPacket.getAddress(), datagramPacket.getPort());
            }, poll);
        }
    }

    private void mapperEvent(byte[] data, InetAddress addressLb, int portLb) {
        var event = Event.parseEvent(new String(data, StandardCharsets.UTF_8).trim());

        routerEvent(event, addressLb,  portLb);
    }

    private void routerEvent(Event event, InetAddress addressLb, int portLb) {
        switch (event.getType()) {
            case PAYMENT_POST -> paymentHandler.receivePayment(PaymentMapper.toPayment(event.getPayload()));
            case PAYMENT_SUMMARY -> paymentHandler.paymentSummary(event.getPayload(), addressLb, portLb);
            case PURGER -> paymentHandler.purgePayments();
        }
    }
}
