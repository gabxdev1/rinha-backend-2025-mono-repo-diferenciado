package br.com.gabxdev.handler;

import br.com.gabxdev.lb.PaymentSummaryWaiter;
import br.com.gabxdev.mapper.EventMapper;
import br.com.gabxdev.producer.EventProducer;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

public class PaymentSummaryHandler implements HttpHandler {

    private final PaymentSummaryWaiter paymentSummaryWaiter = PaymentSummaryWaiter.getInstance();

    private final EventProducer eventProducer = EventProducer.getInstance();

    public PaymentSummaryHandler() {
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        handlePaymentSummary(exchange);
    }

    private void handlePaymentSummary(HttpServerExchange exchange) {
        exchange.dispatch(() -> {
            var queryParameters = exchange.getQueryParameters();

            var from = queryParameters.containsKey("from") ?
                    queryParameters.get("from").getFirst() : "x";

            var to = queryParameters.containsKey("to") ?
                    queryParameters.get("to").getFirst() : "x";

            eventProducer.sendEvent(EventMapper.toPaymentSummaryGetRequest(from, to));

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.setStatusCode(StatusCodes.OK);
            exchange.getResponseSender().send(paymentSummaryWaiter.awaitResponse());
        });
    }
}
