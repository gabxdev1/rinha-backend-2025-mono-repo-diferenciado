package br.com.gabxdev.lb;

import br.com.gabxdev.socket.BackendAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LoudBalance {

    private final static LoudBalance INSTANCE = new LoudBalance();

    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);

    private LoudBalance() {
    }

    public BackendAddress selectBackEnd(List<BackendAddress> sessions) {
        int index = roundRobinIndex.getAndIncrement() % sessions.size();

        return sessions.get(index);
    }

    public static LoudBalance getInstance() {
        return INSTANCE;
    }
}
