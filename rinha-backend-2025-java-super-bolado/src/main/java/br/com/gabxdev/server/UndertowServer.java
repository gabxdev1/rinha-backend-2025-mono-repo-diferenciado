package br.com.gabxdev.server;

import br.com.gabxdev.config.RouterConfig;
import br.com.gabxdev.config.ServerConfig;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.xnio.Options;

import java.util.concurrent.ThreadPoolExecutor;

public class UndertowServer {
    private final static UndertowServer instance = new UndertowServer();

    private final Undertow server;

    private UndertowServer() {
        var serverConfig = ServerConfig.getInstance();

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor();
        threadPoolExecutor.setThreadFactory(Thread.ofVirtual().factory());

        this.server = Undertow.builder()
                .addHttpListener(Integer.parseInt(System.getenv("SERVER_PORT")), "0.0.0.0")
                .setIoThreads(serverConfig.getIoThreadPoolSize())
                .setWorkerThreads(serverConfig.getWorkersThreadPoolSize())
                .setHandler(RouterConfig.getInstance().getRoutes())
//                .setDirectBuffers(true) // Evita cópia extra, usa buffers diretos
//                .setBufferSize(1024) //
                .setSocketOption(Options.TCP_NODELAY, true) // Desliga Nagle, envia logo
                .setSocketOption(Options.REUSE_ADDRESSES, true) // Reuso rápido de porta
                .setSocketOption(Options.BACKLOG, 1024) // Evita refusals sob carga
                .setSocketOption(Options.KEEP_ALIVE, true) // Conexões persistentes
                .setSocketOption(Options.READ_TIMEOUT, 0) // Evita fechamento prematuro
                .setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, true)
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
