package br.com.gabxdev.handler;

import br.com.gabxdev.mapper.PaymentMapper;
import br.com.gabxdev.ws.Event;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

@Component
public class LoadBalanceHandler implements WebSocketHandler {

    private final PaymentHandler paymentHandler;

    public LoadBalanceHandler(PaymentHandler paymentHandler) {
        this.paymentHandler = paymentHandler;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Sinks.Many<String> sink = Sinks.many()
                .multicast()
                .onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

        var receive = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(json -> processEvent(json, sink))
                .onErrorContinue((err, obj) -> System.out.println(err.getMessage()))
                .then();

        var send = session.send(sink
                .asFlux()
                .map(session::textMessage)
                .onBackpressureBuffer());

        return Mono.zip(receive, send).then();
    }

    private void processEvent(String eventJson, Sinks.Many<String> sink) {
        var event = Event.parseEvent(eventJson);

        switch (event.getType()) {
            case PAYMENT_POST -> paymentHandler.receivePayment(PaymentMapper.toPayment(event.getPayload()));
            case PAYMENT_SUMMARY -> paymentHandler.paymentSummary(event, sink);
            case PURGER -> paymentHandler.purgePayments();
        }
    }
}
