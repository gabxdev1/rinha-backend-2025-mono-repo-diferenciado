package br.com.gabxdev.lb;

import br.com.gabxdev.socket.BackendAddress;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoudBalance {

    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);

    public BackendAddress selectBackEnd(List<BackendAddress> sessions) {
        int index = roundRobinIndex.getAndIncrement() % sessions.size();

        return sessions.get(index);
    }
}
