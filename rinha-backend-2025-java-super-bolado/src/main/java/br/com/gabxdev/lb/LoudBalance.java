package br.com.gabxdev.lb;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LoudBalance {

    private final static LoudBalance INSTANCE = new LoudBalance();

    private LoudBalance() {
    }

    public static LoudBalance getInstance() {
        return INSTANCE;
    }

    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);

    private final List<Integer> apis = List.of(1, 2);

    public Integer selectBackEnd() {
        int index = roundRobinIndex.getAndIncrement() % apis.size();

        return apis.get(index);
    }
}
