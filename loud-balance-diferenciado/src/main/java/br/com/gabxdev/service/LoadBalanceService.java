package br.com.gabxdev.service;

import br.com.gabxdev.router.SocketRouter;
import br.com.gabxdev.mapper.EventMapper;
import org.springframework.stereotype.Service;

@Service
public class LoadBalanceService {

    private final SocketRouter socketRouter;

    private final EventMapper eventMapper;

    public LoadBalanceService(SocketRouter socketRouter, EventMapper eventMapper) {
        this.socketRouter = socketRouter;
        this.eventMapper = eventMapper;

    }

    public void receivePaymentHandler(String json) {
        socketRouter.sendToAnyBackend(eventMapper.toPaymentPostRequest(json));
    }

    public void purgePaymentsHandler() {
        socketRouter.sendToAnyBackend(eventMapper.toPurgePaymentsPostRequest());
    }

    public void paymentSummaryHandler(String payload) {
        socketRouter.sendToAnyBackend(payload);
    }
}
