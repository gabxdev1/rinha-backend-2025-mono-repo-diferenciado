package br.com.gabxdev.router;

import br.com.gabxdev.config.BackendUrlConfig;
import br.com.gabxdev.lb.Event;
import br.com.gabxdev.lb.LoudBalance;
import br.com.gabxdev.lb.ResponseWaiter;
import br.com.gabxdev.socket.BackendAddress;
import io.netty.buffer.ByteBuf;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketRouter {

    private final List<SocketAddress> sessions = new CopyOnWriteArrayList<>();

    private final DatagramChannel channel;

    private final LoudBalance loudBalance;

    private final ResponseWaiter responseWaiter;

    private final BackendUrlConfig backendUrlConfig;

    public SocketRouter(
            DatagramChannel channel, LoudBalance loudBalance,
            ResponseWaiter responseWaiter, BackendUrlConfig backendUrlConfig) {
        this.channel = channel;
        this.loudBalance = loudBalance;
        this.responseWaiter = responseWaiter;
        this.backendUrlConfig = backendUrlConfig;
    }

    @PostConstruct
    public void connect() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (BackendAddress address : backendUrlConfig.getBackendsAddresses()) {
            connectToBackend(address);
        }
    }

    private void connectToBackend(BackendAddress address) {
        var inetSocketAddress = new InetSocketAddress(address.url(), address.port());

        sessions.add(inetSocketAddress);
    }

    public void sendToAnyBackend(byte[] eventBytes) {
        var session = loudBalance.selectBackEnd(sessions);

        try {
            channel.send(ByteBuffer.wrap(eventBytes), session);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao enviar", e);
        }
    }

    public void processEvent(String json) {
        var event = Event.parseEvent(json);

        responseWaiter.completeResponse(event.getId(), event.getPayload());
    }
}
