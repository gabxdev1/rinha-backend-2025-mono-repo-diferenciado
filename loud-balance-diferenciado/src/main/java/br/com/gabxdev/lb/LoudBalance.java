package br.com.gabxdev.lb;

import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoudBalance {

    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);

    public SocketAddress selectBackEnd(List<SocketAddress> sessions) {
        int index = roundRobinIndex.getAndIncrement() % sessions.size();

        return sessions.get(index);
    }
}
