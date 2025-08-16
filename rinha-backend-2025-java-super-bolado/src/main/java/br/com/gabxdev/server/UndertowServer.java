package br.com.gabxdev.server;

import br.com.gabxdev.config.RouterConfig;
import br.com.gabxdev.config.ServerConfig;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.xnio.Options;

public class UndertowServer {
    private final static UndertowServer instance = new UndertowServer();

    private final Undertow server;

    private UndertowServer() {
        var serverConfig = ServerConfig.getInstance();

        this.server = Undertow.builder()
                .addHttpListener(Integer.parseInt(System.getenv("SERVER_PORT")), "0.0.0.0")
                .setIoThreads(serverConfig.getIoThreadPoolSize())
                .setWorkerThreads(serverConfig.getWorkersThreadPoolSize())
                .setHandler(RouterConfig.getInstance().getRoutes())
                .setDirectBuffers(true)
                .setSocketOption(Options.TCP_NODELAY, true)
                .setSocketOption(Options.REUSE_ADDRESSES, true)
                .setSocketOption(Options.KEEP_ALIVE, true)
                .setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, true)
                .build();


        Runtime.getRuntime().addShutdownHook(
                Thread.ofVirtual().unstarted(() -> {
                    server.stop();
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
