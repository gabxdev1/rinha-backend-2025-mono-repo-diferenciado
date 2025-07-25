package br.com.gabxdev.router;

import br.com.gabxdev.config.BackendUrlConfig;
import br.com.gabxdev.lb.Event;
import br.com.gabxdev.lb.LoudBalance;
import br.com.gabxdev.lb.ResponseWaiter;
import br.com.gabxdev.socket.BackendAddress;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class SocketRouter {

    private final LoudBalance loudBalance;

    private final ResponseWaiter responseWaiter;

    private final BackendUrlConfig backendUrlConfig;

    public SocketRouter(
                           LoudBalance loudBalance,
                           ResponseWaiter responseWaiter, BackendUrlConfig backendUrlConfig) {
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

    }

    public void sendToAnyBackend(String eventDTO) {
        var session = loudBalance.selectBackEnd(backendUrlConfig.getBackendsAddresses());

    }

    public void processEvent(String json) {
        var event = Event.parseEvent(json);

        responseWaiter.completeResponse(event.getId(), event.getPayload());
    }
}
