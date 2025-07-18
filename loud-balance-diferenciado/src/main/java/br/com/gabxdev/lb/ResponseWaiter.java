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
    private final Map<String, MonoSink<String>> pendingResponses = new ConcurrentHashMap<>();

    public Mono<String> awaitResponse(String correlationId, Duration timeout) {
        return Mono.<String>create(sink -> pendingResponses.put(correlationId, sink))
                .timeout(timeout)
                .doFinally(signal -> pendingResponses.remove(correlationId));
    }

    public void completeResponse(String correlationId, String responseJson) {
        Optional.ofNullable(pendingResponses.remove(correlationId))
                .ifPresent(sink -> sink.success(responseJson));
    }
}
