package br.com.gabxdev.config;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;
import org.newsclub.net.unix.AFGenericSocketAddress;
import org.newsclub.net.unix.AFSocketType;
import org.newsclub.net.unix.AFUNIXDatagramSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

public final class UnixSocketConfig {

    private final static UnixSocketConfig INSTANCE = new UnixSocketConfig();

    private final AFUNIXDatagramSocket socket;

    private UnixSocketConfig() {
        var applicationProperties = ApplicationProperties.getInstance();

        var socketPath = applicationProperties.getProperty(PropertiesKey.SOCKET_PATH);

        System.out.println("SOCKET_PATH: " + socketPath);

        var file = new File(socketPath);

        if (file.exists()) {
            file.delete();
        }

        this.socket = loadDatagramSocket(file);
    }

    private AFUNIXDatagramSocket loadDatagramSocket(File file) {
        AFUNIXDatagramSocket socket;

        try {
            socket = AFUNIXDatagramSocket.newInstance(AFSocketType.SOCK_DGRAM);
            socket.bind(new AFUNIXSocketAddress(file));
            socket.setReceiveBufferSize(5 * 1024 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        startShutdownHook(socket);

        return socket;
    }

    private void startShutdownHook(DatagramSocket channel) {
        Runtime.getRuntime().addShutdownHook(
                Thread.ofVirtual().unstarted(channel::close)
        );
    }

    public static UnixSocketConfig getInstance() {
        return INSTANCE;
    }

    public AFUNIXDatagramSocket getSocket() {
        return socket;
    }
}
