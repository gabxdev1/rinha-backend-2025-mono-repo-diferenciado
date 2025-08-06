package br.com.gabxdev.handler;

import br.com.gabxdev.config.ServerConfig;
import br.com.gabxdev.mapper.EventMapper;
import br.com.gabxdev.producer.EventProducer;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class PurgePaymentHandler implements HttpHandler {

    private final EventProducer eventProducer = EventProducer.getInstance();

    private final ExecutorService threadPool = ServerConfig.getInstance().getWorkersThreadPool();

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        handlePurgePayment(exchange);
    }

    private void handlePurgePayment(HttpServerExchange exchange) {
        exchange.setStatusCode(StatusCodes.OK);
        exchange.endExchange();

        CompletableFuture.runAsync(() -> {
            eventProducer.sendEvent(EventMapper.toPurgePaymentsPostRequest());
        }, threadPool);
    }
}
