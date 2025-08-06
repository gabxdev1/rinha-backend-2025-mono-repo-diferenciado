package br.com.gabxdev.config;

import org.newsclub.net.unix.AFSocketType;
import org.newsclub.net.unix.AFUNIXDatagramSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;
import java.net.SocketException;
import java.util.List;

public class ApiChannelConfig {

    private final static ApiChannelConfig INSTANCE = new ApiChannelConfig();

    private final AFUNIXDatagramSocket socketApi1;
    private final AFUNIXDatagramSocket socketApi2;

    private ApiChannelConfig() {
        AFUNIXSocketAddress addressApi1;
        AFUNIXSocketAddress addressApi2;


        try {
            addressApi1 = new AFUNIXSocketAddress(new File("/tmp/api1.sock"));
            addressApi2 = new AFUNIXSocketAddress(new File("/tmp/api2.sock"));
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        socketApi1 = createSocket(addressApi1);
        socketApi2 = createSocket(addressApi2);


        AFUNIXSocketAddress lbAddress;
        try {
            var file = new File("/tmp/loudbalance.sock");
            file.delete();
            lbAddress = new AFUNIXSocketAddress(file);
            socketApi1.bind(lbAddress);
        } catch (Exception e) {
            System.out.println("Error binding file: " + e.getMessage());
        }

        startShutdownHook(socketApi1);
        startShutdownHook(socketApi2);

        if (new File("/tmp/loudbalance.sock").exists()) {
            System.out.println("file exists");
        } else {
            System.out.println("file not exists");
        }
    }

    public static ApiChannelConfig getInstance() {
        return INSTANCE;
    }

    private AFUNIXDatagramSocket createSocket(AFUNIXSocketAddress address) {
        try {
            var socket = AFUNIXDatagramSocket.newInstance(AFSocketType.SOCK_DGRAM);
            socket.connect(address);
            socket.setSendBufferSize(5 * 1024 * 1024);
            return socket;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar socket UDS", e);
        }
    }

    private void startShutdownHook(AFUNIXDatagramSocket socket) {
        Runtime.getRuntime().addShutdownHook(
                Thread.ofVirtual().unstarted(() -> {
                    try {
                        socket.close();
                    } catch (Exception ignored) {
                    }
                })
        );
    }

    public List<AFUNIXDatagramSocket> getDatagramSockets() {
        return List.of(socketApi1, socketApi2);
    }
}
