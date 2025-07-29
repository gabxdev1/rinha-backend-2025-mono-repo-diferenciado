package br.com.gabxdev.server;

import br.com.gabxdev.config.ServerConfig;
import br.com.gabxdev.handler.PaymentSummaryHandler;
import br.com.gabxdev.handler.PurgePaymentHandler;
import br.com.gabxdev.handler.ReceivePaymentHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class RinhaHttpServer {
    private final static RinhaHttpServer instance = new RinhaHttpServer();

    private final ServerConfig serverConfig = ServerConfig.getInstance();

    private RinhaHttpServer() {
    }

    public void start() {
        try {
            var server = HttpServer.create(new InetSocketAddress(9999), 0);
            server.setExecutor(serverConfig.getWorkersThreadPool());

            server.createContext("/payments-summary", new PaymentSummaryHandler());
            server.createContext("/payments", new ReceivePaymentHandler());
            server.createContext("/purge-payments", new PurgePaymentHandler());


            server.start();
            System.out.println("Server started on port " + 9999);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                server.stop(0);
                serverConfig.getWorkersThreadPool().shutdown();
            }));

            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static RinhaHttpServer getInstance() {
        return instance;
    }
}
