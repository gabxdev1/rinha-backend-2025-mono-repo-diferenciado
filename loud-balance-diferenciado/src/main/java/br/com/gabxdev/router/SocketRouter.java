package br.com.gabxdev.router;

import br.com.gabxdev.config.BackendUrlConfig;
import br.com.gabxdev.lb.LoudBalance;
import br.com.gabxdev.lb.ResponseWaiter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

@Component
public class SocketRouter {

    private final DatagramSocket datagramSocket;

    private final LoudBalance loudBalance;

    private final ResponseWaiter responseWaiter;

    private final BackendUrlConfig backendUrlConfig;

    public SocketRouter(DatagramSocket datagramSocket, LoudBalance loudBalance,
                        ResponseWaiter responseWaiter, BackendUrlConfig backendUrlConfig) {
        this.datagramSocket = datagramSocket;
        this.loudBalance = loudBalance;
        this.responseWaiter = responseWaiter;
        this.backendUrlConfig = backendUrlConfig;

        start();
    }

    private void start() {
        Thread.startVirtualThread(this::handleEvents);
    }

    private void handleEvents() {
        while (true) {
            try {
                var buffer = new byte[200];

                var datagramPacket = new DatagramPacket(buffer, buffer.length);

                datagramSocket.receive(datagramPacket);

                var event = new String(datagramPacket.getData(), StandardCharsets.UTF_8).trim();

                processEvent(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void sendToAnyBackend(byte[] eventBytes) {
        var session = loudBalance.selectBackEnd(backendUrlConfig.getBackendsAddresses());

        try {
            datagramSocket.send(new DatagramPacket(eventBytes,
                    eventBytes.length,
                    InetAddress.getByName(session.url()),
                    session.port()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processEvent(String json) {
        responseWaiter.completeResponse(json);
    }
}
