package br.com.gabxdev.handler;

import br.com.gabxdev.client.UdpClient;
import br.com.gabxdev.config.SocketInternalConfig;
import br.com.gabxdev.lb.LoudBalance;
import br.com.gabxdev.mapper.EventMapper;
import br.com.gabxdev.mapper.PaymentMapper;
import br.com.gabxdev.worker.PaymentWorker;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ReceivePaymentHandler implements HttpHandler {

    private final LoudBalance loudBalance = LoudBalance.getInstance();

    private final PaymentWorker paymentWorker = PaymentWorker.getInstance();

    private final UdpClient udpClient = UdpClient.getInstance();

    private final DatagramSocket socket = SocketInternalConfig.getInstance().getDatagramSocket();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var method = exchange.getRequestMethod();

        if ("POST".equals(method)) {
            handleReceivePayment(exchange);
        }
    }

    private void handleReceivePayment(HttpExchange exchange) throws IOException {
        var payload = readRequestBody(exchange);

        sendResponse(exchange, 200);

        if (loudBalance.selectBackEnd() == 1) {
            processPaymentInternal(payload);
        } else {
            processPaymentExternal(payload);
        }

    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (var is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processPaymentInternal(String payload) {
        paymentWorker.enqueue(PaymentMapper.toPaymentInternal(payload));
    }

    private void processPaymentExternal(String payload) {
        var body = EventMapper.toPaymentPostRequest(payload)
                .getBytes(StandardCharsets.UTF_8);

        udpClient.send(new DatagramPacket(body, body.length), socket);
    }

    private void sendResponse(HttpExchange exchange, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, 0);
        exchange.getResponseBody().close();
    }
}
