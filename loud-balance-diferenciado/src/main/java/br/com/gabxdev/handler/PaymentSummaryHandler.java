package br.com.gabxdev.handler;

import br.com.gabxdev.config.ServerConfig;
import br.com.gabxdev.lb.PaymentSummaryWaiter;
import br.com.gabxdev.mapper.EventMapper;
import br.com.gabxdev.service.LoadBalanceService;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.util.concurrent.ExecutorService;

public final class PaymentSummaryHandler implements HttpHandler {

    private final PaymentSummaryWaiter paymentSummaryWaiter = PaymentSummaryWaiter.getInstance();

    private final LoadBalanceService loadBalanceService = LoadBalanceService.getInstance();

    private final ExecutorService threadPool = ServerConfig.getInstance().getWorkersThreadPool();

    public PaymentSummaryHandler() {
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        var method = exchange.getRequestMethod().toString();

        if (method.equals("GET")) {
            handlePaymentSummary(exchange);
        }
    }

    private void handlePaymentSummary(HttpServerExchange exchange) {
        exchange.dispatch(threadPool, () -> {
            var queryParameters = exchange.getQueryParameters();

            var from = queryParameters.containsKey("from") ?
                    queryParameters.get("from").getFirst() : "";

            var to = queryParameters.containsKey("to") ?
                    queryParameters.get("to").getFirst() : "";

            loadBalanceService.paymentSummaryHandler(EventMapper.toPaymentSummaryGetRequest(from, to));

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.setStatusCode(StatusCodes.OK);
            exchange.getResponseSender().send(paymentSummaryWaiter.awaitResponse());
        });
    }
}
