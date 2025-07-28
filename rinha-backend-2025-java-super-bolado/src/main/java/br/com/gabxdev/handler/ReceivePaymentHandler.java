package br.com.gabxdev.handler;

import br.com.gabxdev.client.UdpClient;
import br.com.gabxdev.config.ServerConfig;
import br.com.gabxdev.config.SocketInternalConfig;
import br.com.gabxdev.lb.LoudBalance;
import br.com.gabxdev.mapper.EventMapper;
import br.com.gabxdev.mapper.PaymentMapper;
import br.com.gabxdev.worker.PaymentWorker;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ReceivePaymentHandler implements HttpHandler {

    private final LoudBalance loudBalance = LoudBalance.getInstance();

    private final ExecutorService threadPool = ServerConfig.getInstance().getWorkersThreadPool();

    private final PaymentWorker paymentWorker = PaymentWorker.getInstance();

    private final UdpClient udpClient = UdpClient.getInstance();

    private final DatagramSocket socket = SocketInternalConfig.getInstance().getDatagramSocket();

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        var method = exchange.getRequestMethod().toString();

        if (method.equals("POST")) {
            handleReceivePayment(exchange);
        }
    }

    private void handleReceivePayment(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString((httpServerExchange, payload) -> {
            CompletableFuture.runAsync(() -> {
                if (loudBalance.selectBackEnd() == 1) {
                    processPaymentInternal(payload);
                } else {
                    processPaymentExternal(payload);
                }
            }, threadPool);
        });
    }

    private void processPaymentInternal(String payload) {
        paymentWorker.enqueue(PaymentMapper.toPaymentInternal(payload));
    }

    private void processPaymentExternal(String payload) {
        var body = EventMapper.toPaymentPostRequest(payload)
                .getBytes(StandardCharsets.UTF_8);

        udpClient.send(new DatagramPacket(body, body.length), socket);
    }
}
