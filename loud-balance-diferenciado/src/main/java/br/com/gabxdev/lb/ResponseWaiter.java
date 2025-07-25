package br.com.gabxdev.lb;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ResponseWaiter {
    private final Map<String, String> pendingResponses = new ConcurrentHashMap<>();

    public String awaitResponse(String correlationId, Duration timeout) {
        return "";
    }

    public void completeResponse(String correlationId, String responseJson) {

    }
}
