package br.com.gabxdev.lb;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.List;

@Component
public class LoudBalance {

    //    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);
//
//    public WebSocketSession selectBackEnd(List<WebSocketSession> sessions) {
//        int index = roundRobinIndex.getAndIncrement() % sessions.size();
//
//        return sessions.get(index);
//    }
    private static final Unsafe UNSAFE;
    private static final long INDEX_OFFSET;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
            INDEX_OFFSET = UNSAFE.objectFieldOffset(LoudBalance.class.getDeclaredField("index"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private volatile int index = 0;

    public WebSocketSession selectBackEnd(List<WebSocketSession> sessions) {
        if (sessions.size() != 2) {
            throw new IllegalArgumentException("Somente 2 backends suportados.");
        }

        var current = UNSAFE.getAndAddInt(this, INDEX_OFFSET, 1);

        return sessions.get(current & 1);
    }
}
