package br.com.gabxdev.server;

import br.com.gabxdev.config.RouterConfig;
import br.com.gabxdev.config.ServerConfig;
import io.undertow.Undertow;

public class UndertowServer {
    private final static UndertowServer instance = new UndertowServer();

    private final Undertow server;

    private UndertowServer() {
        var serverConfig = ServerConfig.getInstance();


        this.server = Undertow.builder()
                .addHttpListener(Integer.parseInt(System.getenv("SERVER_PORT")), "0.0.0.0")
                .setIoThreads(serverConfig.getIoThreadPoolSize())
                .setWorkerThreads(serverConfig.getWorkersThreadPoolSize())
                .setDirectBuffers(true)
                .setHandler(RouterConfig.getInstance().getRoutes())
                .build();


        Runtime.getRuntime().addShutdownHook(
                Thread.ofVirtual().unstarted(() -> {
                    server.stop();
                    System.out.println("Server shutting down...");
                })
//                serverConfig.getWorkersThreadPool().shutdown();
        );
    }

    public void start() {
        this.server.start();
    }

    public static UndertowServer getInstance() {
        return instance;
    }
}
