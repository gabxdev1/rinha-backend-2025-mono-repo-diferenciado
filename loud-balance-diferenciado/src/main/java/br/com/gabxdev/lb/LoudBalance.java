package br.com.gabxdev.lb;

import org.newsclub.net.unix.AFUNIXDatagramSocket;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LoudBalance {

    private final static LoudBalance INSTANCE = new LoudBalance();

    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);

    private LoudBalance() {
    }

    public AFUNIXDatagramSocket selectBackEnd(List<AFUNIXDatagramSocket> sockets) {
        int index = roundRobinIndex.getAndIncrement() % sockets.size();

        return sockets.get(index);
    }

    public static LoudBalance getInstance() {
        return INSTANCE;
    }
}
