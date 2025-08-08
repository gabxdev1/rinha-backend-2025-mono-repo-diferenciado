package br.com.gabxdev.server;

import br.com.gabxdev.config.RouterConfig;
import br.com.gabxdev.config.ServerConfig;
import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;
import io.undertow.Undertow;
import org.xnio.*;
import org.xnio.channels.AcceptingChannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.UnixDomainSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UndertowServer {
    private final static UndertowServer instance = new UndertowServer();

    private final Undertow server;

    private UndertowServer() {
        var properties = ApplicationProperties.getInstance();
        var socketPath = properties.getProperty(PropertiesKey.SOCKET_PATH);
        var serverConfig = ServerConfig.getInstance();

        var socketFile = Paths.get(socketPath);

        if (Files.exists(socketFile)) {
            try {
                Files.delete(socketFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try {
            // Obter instância do XNIO
            Xnio xnio = Xnio.getInstance();
            XnioWorker worker = xnio.createWorker(OptionMap.builder()
                    .set(Options.WORKER_IO_THREADS, 2)
                    .set(Options.CONNECTION_HIGH_WATER, 1000000)
                    .set(Options.CONNECTION_LOW_WATER, 1000000)
                    .set(Options.WORKER_TASK_CORE_THREADS, 30)
                    .set(Options.WORKER_TASK_MAX_THREADS, 30)
                    .set(Options.TCP_NODELAY, true)
                    .set(Options.CORK, true)
                    .getMap());

            // Criar servidor Undertow sem listeners
            Undertow server = Undertow.builder()
                    .setHandler(RouterConfig.getInstance().getRoutes())
                    .setWorker(worker)
                    .build();

            // Criar socket address Unix Domain

            SocketAddress socketAddress = UnixDomainSocketAddress.of(socketPath);

            // Abrir canal de aceitação Unix Domain Socket
            AcceptingChannel<StreamConnection> acceptingChannel = worker.createStreamConnectionServer(
                    socketAddress,
                    null, // acceptListener será definido depois
                    OptionMap.EMPTY
            );

            // Configurar listener de aceitação
            acceptingChannel.getAcceptSetter().set(channel -> {
                try {
                    // Processar conexão através do Undertow
                    server.getListenerInfo().get(0).getSslContext().handleRequest(channel);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        channel.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            // Iniciar servidor
            server.start();
            acceptingChannel.resumeAccepts();

            System.out.println("Servidor iniciado no Unix Domain Socket: " + socketPath);

            // Hook de shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    acceptingChannel.close();
                    server.stop();
                    worker.shutdown();
                    if (Files.exists(socketFile)) {
                        Files.delete(socketFile);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            // Manter servidor rodando
            Thread.currentThread().join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static UndertowServer getInstance() {
        return instance;
    }
}
