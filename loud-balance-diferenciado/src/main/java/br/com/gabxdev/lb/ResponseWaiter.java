package br.com.gabxdev.lb;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ResponseWaiter {
    private final ArrayBlockingQueue<String> pendingSummaryResponse = new ArrayBlockingQueue<>(1);

    public String awaitResponse(String correlationId, Duration timeout) {
        return "";
    }

    public void completeResponse(String correlationId, String responseJson) {

    }
}
