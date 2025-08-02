package br.com.gabxdev.service;

import br.com.gabxdev.mapper.EventMapper;
import br.com.gabxdev.router.SocketRouter;

import java.nio.charset.StandardCharsets;

public class LoadBalanceService {

    private final static LoadBalanceService INSTANCE = new LoadBalanceService();

    private LoadBalanceService() {
    }

    public static LoadBalanceService getInstance() {
        return INSTANCE;
    }

    private final SocketRouter socketRouter = SocketRouter.getInstance();

    public void receivePaymentHandler(byte[] payload) {
        socketRouter.sendToAnyBackend(EventMapper.toPaymentPostRequest(payload));
    }

    public void purgePaymentsHandler() {
        socketRouter.sendToAnyBackend(EventMapper.toPurgePaymentsPostRequest());
    }

    public void paymentSummaryHandler(byte[] payload) {
        socketRouter.sendToAnyBackend(payload);
    }
}
