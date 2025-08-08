package br.com.gabxdev.handler;

import br.com.gabxdev.service.PaymentService;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

public class PurgePaymentHandler implements HttpHandler {

    private final PaymentService paymentService = PaymentService.getInstance();


//    private final ExecutorService threadPool = ServerConfig.getInstance().getWorkersThreadPool();

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

//        CompletableFuture.runAsync(() -> {
        paymentService.purgePayments();
//        }, threadPool);
    }
}
