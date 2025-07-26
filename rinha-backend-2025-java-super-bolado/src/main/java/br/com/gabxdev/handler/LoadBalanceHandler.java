package br.com.gabxdev.handler;

import br.com.gabxdev.config.LoadBalanceClient;
import br.com.gabxdev.mapper.PaymentMapper;
import br.com.gabxdev.ws.Event;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class LoadBalanceHandler {

    private final PaymentHandler paymentHandler;

    private final DatagramSocket datagramSocket;

    ExecutorService poll = Executors.newFixedThreadPool(3, Thread.ofVirtual().factory());

    public LoadBalanceHandler(PaymentHandler paymentHandler, DatagramSocket datagramSocket) {
        this.paymentHandler = paymentHandler;
        this.datagramSocket = datagramSocket;
    }

    @PostConstruct
    public void init() {
        Thread.startVirtualThread(() -> {
            try {
                handleEvents();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void handleEvents() throws IOException {
        while (true) {
            var buffer = new byte[60];

            var datagramPacket = new DatagramPacket(buffer, buffer.length);

            datagramSocket.receive(datagramPacket);

            CompletableFuture.runAsync(() -> {
                processEvent(new String(datagramPacket.getData(),
                                StandardCharsets.UTF_8).trim(),
                        new LoadBalanceClient(datagramPacket.getAddress(), datagramPacket.getPort()));
            }, poll);
        }
    }

    private void processEvent(String eventJson, LoadBalanceClient client) {
        var event = Event.parseEvent(eventJson);

        switch (event.getType()) {
            case PAYMENT_POST -> paymentHandler.receivePayment(PaymentMapper.toPayment(event.getPayload()));
            case PAYMENT_SUMMARY -> paymentHandler.paymentSummary(event.getPayload(), client);
            case PURGER -> paymentHandler.purgePayments();
        }
    }
}
