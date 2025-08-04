package br.com.gabxdev.lb;

import br.com.gabxdev.socket.BackendAddress;

import java.net.DatagramSocket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LoudBalance {

    private final static LoudBalance INSTANCE = new LoudBalance();

    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);

    private LoudBalance() {
    }

    public DatagramSocket selectBackEnd(List<DatagramSocket> sockets) {
        int index = roundRobinIndex.getAndIncrement() % sockets.size();

        return sockets.get(index);
    }

    public static LoudBalance getInstance() {
        return INSTANCE;
    }
}
