package br.com.gabxdev.config;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;
import org.newsclub.net.unix.AFSocketType;
import org.newsclub.net.unix.AFUNIXDatagramSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.List;

public final class UnixSocketConfig {

    private final static UnixSocketConfig INSTANCE = new UnixSocketConfig();

    private final AFUNIXDatagramSocket socketOne;

    private final AFUNIXDatagramSocket socketTwo;

    private UnixSocketConfig() {
        var applicationProperties = ApplicationProperties.getInstance();

        var socketPath = applicationProperties.getProperty(PropertiesKey.SOCKET_PATH);
        var socketPath2 = applicationProperties.getProperty(PropertiesKey.SOCKET_PATH_2);

        var file = new File(socketPath);
        var file2 = new File(socketPath2);

        if (file.exists()) {
            file.delete();
        }

        if (file2.exists()) {
            file2.delete();
        }

        this.socketOne = loadDatagramSocket(file);
        this.socketTwo = loadDatagramSocket(file2);

        startShutdownHook(List.of(this.socketOne, this.socketTwo));
    }

    private AFUNIXDatagramSocket loadDatagramSocket(File file) {
        AFUNIXDatagramSocket socket;

        try {
            socket = AFUNIXDatagramSocket.newInstance(AFSocketType.SOCK_DGRAM);
            socket.bind(AFUNIXSocketAddress.of(file));
            socket.setReceiveBufferSize(5 * 1024 * 1024);
            System.out.println("[UnixSocketConfig] new socket UDS created, address: " + socket.getLocalSocketAddress());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return socket;
    }

    private void startShutdownHook(List<DatagramSocket> channel) {
        Runtime.getRuntime().addShutdownHook(
                Thread.ofVirtual().unstarted(() ->
                        channel.forEach(DatagramSocket::close))
        );
    }

    public static UnixSocketConfig getInstance() {
        return INSTANCE;
    }

    public List<AFUNIXDatagramSocket> getSockets() {
        return List.of(this.socketOne, this.socketTwo);
    }
}
