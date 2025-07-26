package br.com.gabxdev.handler;

import br.com.gabxdev.config.DatagramSocketConfig;
import br.com.gabxdev.dto.Event;
import br.com.gabxdev.mapper.PaymentMapper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class LoadBalanceHandler {

    private final static LoadBalanceHandler INSTANCE = new LoadBalanceHandler();

    private final PaymentHandler paymentHandler = PaymentHandler.getInstance();

    private final DatagramSocket datagramSocket = DatagramSocketConfig.getInstance().getDatagramSocket();

    ExecutorService poll = Executors.newFixedThreadPool(3, Thread.ofVirtual().factory());

    private LoadBalanceHandler() {
        Thread.startVirtualThread(this::start);
    }

    public static LoadBalanceHandler getInstance() {
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


        while (true) {
            var buffer = new byte[60];

            var datagramPacket = new DatagramPacket(buffer, buffer.length);

            datagramSocket.receive(datagramPacket);

            var data = new String(datagramPacket.getData(), StandardCharsets.UTF_8).trim();

            CompletableFuture.runAsync(() -> {
                processEvent(data, datagramPacket.getAddress(), datagramPacket.getPort());
            }, poll);
        }
    }

    private void processEvent(String eventJson, InetAddress addressLb, int portLb) {
        var event = Event.parseEvent(eventJson);

        switch (event.getType()) {
            case PAYMENT_POST -> paymentHandler.receivePayment(PaymentMapper.toPayment(event.getPayload()));
            case PAYMENT_SUMMARY -> paymentHandler.paymentSummary(event.getPayload(), addressLb, portLb);
            case PURGER -> paymentHandler.purgePayments();
        }
    }
}
