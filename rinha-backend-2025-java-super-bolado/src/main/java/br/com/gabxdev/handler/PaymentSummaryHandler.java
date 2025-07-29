package br.com.gabxdev.handler;

import br.com.gabxdev.commons.HttpHeaders;
import br.com.gabxdev.commons.MediaType;
import br.com.gabxdev.service.PaymentService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class PaymentSummaryHandler implements HttpHandler {

    private final PaymentService paymentService = PaymentService.getInstance();

    public PaymentSummaryHandler() {
    }

    @Override
    public void handle(HttpExchange exchange) {
        var method = exchange.getRequestMethod();

        if ("GET".equals(method)) {
            handlePaymentSummary(exchange);
        }
    }

    private void handlePaymentSummary(HttpExchange exchange) {
        try {
            var queryParams = parseQueryParameters(exchange.getRequestURI().getQuery());

            var from = queryParams.getOrDefault("from", "");
            var to = queryParams.getOrDefault("to", "");

            var paymentSummaryResponse = paymentService.getPaymentSummary(from, to);

            sendJsonResponse(exchange, 200, paymentSummaryResponse);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                sendResponse(exchange, 500, "Internal Server Error");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private Map<String, String> parseQueryParameters(String query) {
        var params = new HashMap<String, String>();

        if (query != null && !query.isEmpty()) {
            var pairs = query.split("&");
            for (var pair : pairs) {
                var keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    try {
                        var key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                        var value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                        params.put(key, value);
                    } catch (Exception e) {
                    }
                }
            }
        }
        return params;
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set(HttpHeaders.CONTENT_TYPE.getValue(), MediaType.APPLICATION_JSON.getValue());
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        try (var os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
