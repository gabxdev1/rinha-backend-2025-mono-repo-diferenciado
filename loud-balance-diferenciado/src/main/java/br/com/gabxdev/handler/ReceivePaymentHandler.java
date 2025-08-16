package br.com.gabxdev.handler;

import br.com.gabxdev.config.ServerConfig;
import br.com.gabxdev.producer.PaymentPostProducer;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ReceivePaymentHandler implements HttpHandler {
    private final PaymentPostProducer paymentPostProducer = PaymentPostProducer.getInstance();

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        handleReceivePayment(exchange);
    }

    private void handleReceivePayment(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullBytes((httpServerExchange, payload) -> {
            var response = paymentPostProducer.callAnyApi(payload);

            exchange.setStatusCode(response);
            exchange.endExchange();
        });
    }
}
