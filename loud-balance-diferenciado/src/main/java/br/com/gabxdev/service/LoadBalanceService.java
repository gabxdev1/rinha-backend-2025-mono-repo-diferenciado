package br.com.gabxdev.service;

import br.com.gabxdev.client.WebSocketRouter;
import br.com.gabxdev.mapper.EventMapper;
import org.springframework.stereotype.Service;

@Service
public class LoadBalanceService {

    private final WebSocketRouter webSocketRouter;

    private final EventMapper eventMapper;

    public LoadBalanceService(WebSocketRouter webSocketRouter, EventMapper eventMapper) {
        this.webSocketRouter = webSocketRouter;
        this.eventMapper = eventMapper;

    }

    public void receivePaymentHandler(String json) {
        webSocketRouter.sendToAnyBackend(eventMapper.toPaymentPostRequest(json));
    }

    public void purgePaymentsHandler() {
        webSocketRouter.sendToAnyBackend(eventMapper.toPurgePaymentsPostRequest());
    }

    public void paymentSummaryHandler(String payload) {
        webSocketRouter.sendToAnyBackend(payload);
    }
}
