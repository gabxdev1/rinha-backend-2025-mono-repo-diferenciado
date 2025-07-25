package br.com.gabxdev.service;

import br.com.gabxdev.mapper.EventMapper;
import br.com.gabxdev.router.SocketRouter;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class LoadBalanceService {

    private final SocketRouter socketRouter;

    private final EventMapper eventMapper;

    public LoadBalanceService(SocketRouter socketRouter, EventMapper eventMapper) {
        this.socketRouter = socketRouter;
        this.eventMapper = eventMapper;

    }

    public void receivePaymentHandler(String payload) {
        var event = eventMapper.toPaymentPostRequest(payload);

        socketRouter.sendToAnyBackend(event.getBytes(StandardCharsets.UTF_8));
    }

    public void purgePaymentsHandler() {
        var event = eventMapper.toPurgePaymentsPostRequest();

        socketRouter.sendToAnyBackend(event.getBytes(StandardCharsets.UTF_8));
    }

    public void paymentSummaryHandler(String payload) {
        socketRouter.sendToAnyBackend(payload.getBytes(StandardCharsets.UTF_8));
    }
}
