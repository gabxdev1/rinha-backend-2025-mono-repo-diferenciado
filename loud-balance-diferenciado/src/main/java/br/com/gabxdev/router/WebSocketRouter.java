package br.com.gabxdev.router;

import br.com.gabxdev.config.BackendUrlConfig;
import br.com.gabxdev.lb.Event;
import br.com.gabxdev.lb.LoudBalance;
import br.com.gabxdev.lb.ResponseWaiter;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketRouter {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

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
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (URI uri : backendUrlConfig.getBackendsURI()) {
            connectToBackend(uri);
        }
    }

    private void connectToBackend(URI uri) {
        client.execute(uri, session -> {
            sessions.add(session);

            return session.receive()
                    .map(WebSocketMessage::getPayloadAsText)
                    .doOnNext(this::processEvent)
                    .doFinally(signalType -> {
                        sessions.remove(session);

                        System.out.println(uri + ": " + signalType);
                    }).then();
        }).retry().subscribe();
    }

    public void sendToAnyBackend(String eventDTO) {
        var session = loudBalance.selectBackEnd(sessions);

        session.send(Mono.just(session.textMessage(eventDTO))).subscribe();
    }

    public void processEvent(String json) {
        var event = Event.parseEvent(json);

        responseWaiter.completeResponse(event.getId(), event.getPayload());
    }
}
