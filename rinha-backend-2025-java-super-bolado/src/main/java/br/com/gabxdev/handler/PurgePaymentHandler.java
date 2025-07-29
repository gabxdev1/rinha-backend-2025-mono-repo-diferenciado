package br.com.gabxdev.handler;

import br.com.gabxdev.service.PaymentService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class PurgePaymentHandler implements HttpHandler {

    private final PaymentService paymentService = PaymentService.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var method = exchange.getRequestMethod();

        if ("POST".equals(method)) {
            handlePurgePayment(exchange);
        }
    }

    private void handlePurgePayment(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 200);

        paymentService.purgePayments();
    }

    private void sendResponse(HttpExchange exchange, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, 0);
        exchange.getResponseBody().close();
    }
}
