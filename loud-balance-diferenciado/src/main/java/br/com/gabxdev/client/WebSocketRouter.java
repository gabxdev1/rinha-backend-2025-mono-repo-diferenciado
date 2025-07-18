package br.com.gabxdev.client;

import br.com.gabxdev.config.BackendUrlConfig;
import br.com.gabxdev.lb.Event;
import br.com.gabxdev.lb.LoudBalance;
import br.com.gabxdev.lb.ResponseWaiter;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketRouter {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private final Map<URI, Sinks.Many<String>> sinks = new ConcurrentHashMap<>();

    private final WebSocketClient client;

    private final LoudBalance loudBalance;

    private final ResponseWaiter responseWaiter;

    private final BackendUrlConfig backendUrlConfig;

    public WebSocketRouter(ReactorNettyWebSocketClient client,
                           LoudBalance loudBalance,
                           ResponseWaiter responseWaiter, BackendUrlConfig backendUrlConfig) {
        this.client = client;
        this.loudBalance = loudBalance;
        this.responseWaiter = responseWaiter;
        this.backendUrlConfig = backendUrlConfig;
    }

    @PostConstruct
    public void connect() {
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (URI uri : backendUrlConfig.getBackendsURI()) {
            connectToBackend(uri);
        }
    }

    private void connectToBackend(URI uri) {

//        Sinks.Many<String> sink = Sinks.many()
//                .multicast()
//                .onBackpressureBuffer();
//
        Sinks.Many<String> sink = Sinks.many()
                .multicast().directAllOrNothing();

        sinks.put(uri, sink);

        client.execute(uri, session -> {
            sessions.add(session);

            var send = session.send(
                    sink.asFlux().map(session::textMessage)
            );

            var receive = session.receive()
                    .map(WebSocketMessage::getPayloadAsText)
                    .doOnNext(this::process)
                    .doFinally(signalType -> {
                        sessions.remove(session);
                        sinks.remove(uri);
                        System.out.println("Conexão encerrada com " + uri + ": " + signalType);
                    }).then();
            return Mono.zip(send, receive).then();
        }).retry().subscribe();
    }

    public void sendToAnyBackend(String eventDTO) {


        var session = loudBalance.selectBackEnd(sessions);

        session.send(Mono.just(session.textMessage(eventDTO))).subscribe();


//        var uri = loudBalance.selectBackEnd(backendUris);
//        var sink = sinks.get(uri);

//        if (sink != null) {
//            synchronized (sink) {
//                sink.emitNext(eventDTO, Sinks.EmitFailureHandler.FAIL_FAST);
//            }
//        } else {
//            System.err.println("api selecionado não está aberto");
//        }
    }

    public void process(String json) {
        var event = Event.parseEvent(json);
        responseWaiter.completeResponse(event.getId(), event.getPayload());
    }
}
