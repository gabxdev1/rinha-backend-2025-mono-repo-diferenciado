package br.com.gabxdev.lb;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoudBalance {

    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);

    public WebSocketSession selectBackEnd(List<WebSocketSession> sessions) {
        int index = roundRobinIndex.getAndIncrement() % sessions.size();

        return sessions.get(index);
    }
}
