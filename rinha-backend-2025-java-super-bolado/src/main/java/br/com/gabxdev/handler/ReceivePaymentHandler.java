package br.com.gabxdev.handler;

import br.com.gabxdev.config.ServerConfig;
import br.com.gabxdev.mapper.PaymentMapper;
import br.com.gabxdev.worker.PaymentWorker;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.util.concurrent.ExecutorService;

public class ReceivePaymentHandler implements HttpHandler {

    private final PaymentWorker paymentWorker = PaymentWorker.getInstance();

    private final ExecutorService pool = ServerConfig.getInstance().getWorkersThreadPool();

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
            exchange.setStatusCode(StatusCodes.OK);
            exchange.endExchange();


//            CompletableFuture.runAsync(() -> {
            paymentWorker.enqueue(PaymentMapper.toPayment(payload));
//            }, pool);
        });
    }
}
