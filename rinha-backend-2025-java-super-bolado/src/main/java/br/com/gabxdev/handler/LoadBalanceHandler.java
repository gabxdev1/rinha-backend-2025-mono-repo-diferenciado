package br.com.gabxdev.handler;

import br.com.gabxdev.mapper.PaymentMapper;
import br.com.gabxdev.ws.Event;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import java.io.IOException;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Component
public class LoadBalanceHandler {

    private final PaymentHandler paymentHandler;

    private final DatagramChannel channel;

    ByteBuffer buffer = ByteBuffer.allocate(1400);

    ExecutorService poll = Executors.newFixedThreadPool(3, Thread.ofVirtual().factory());

    public LoadBalanceHandler(PaymentHandler paymentHandler, DatagramChannel channel) {
        this.paymentHandler = paymentHandler;
        this.channel = channel;
    }

    @PostConstruct
    public void handle() throws IOException {
        while (true) {
            buffer.clear();
            channel.receive(buffer);
            buffer.flip();

            var data = new byte[buffer.remaining()];
            buffer.get(data);

            CompletableFuture.runAsync(() -> {
                processEvent(new String(data, StandardCharsets.UTF_8));
            }, poll);
        }
    }

    private void processEvent(String eventJson) {
        var event = Event.parseEvent(eventJson);

        switch (event.getType()) {
            case PAYMENT_POST -> paymentHandler.receivePayment(PaymentMapper.toPayment(event.getPayload()));
            case PAYMENT_SUMMARY -> paymentHandler.paymentSummary(event.getPayload());
            case PURGER -> paymentHandler.purgePayments();
        }
    }
}
