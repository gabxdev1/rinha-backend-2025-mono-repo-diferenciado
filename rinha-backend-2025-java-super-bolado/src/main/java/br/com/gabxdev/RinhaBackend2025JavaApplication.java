package br.com.gabxdev;

import br.com.gabxdev.client.PaymentProcessorClient;
import br.com.gabxdev.config.*;
import br.com.gabxdev.consumer.EventConsumer;
import br.com.gabxdev.consumer.PaymentPostConsumer;
import br.com.gabxdev.middleware.PaymentMiddleware;
import br.com.gabxdev.middleware.PaymentSummaryWaiter;
import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.repository.InMemoryPaymentDatabase;
import br.com.gabxdev.router.ApiRouter;
import br.com.gabxdev.service.PaymentService;
import br.com.gabxdev.worker.PaymentWorker;

public class RinhaBackend2025JavaApplication {
    public static void main(String[] args) throws InterruptedException {
        start();
    }

    private static void start() throws InterruptedException {
        ApplicationProperties.getInstance();
        ApiInternalConfig.getInstance();
        UnixSocketConfig.getInstance();
        ApiSockerInternalConfig.getInstance();
        HttpClientConfig.httpClient();
        PaymentProcessorConfig.getInstance();
        InMemoryPaymentDatabase.getInstance();
        PaymentProcessorClient.getInstance();
        PaymentWorker.getInstance();
        PaymentSummaryWaiter.getInstance();
        PaymentMiddleware.getInstance();
        PaymentService.getInstance();
        ApiRouter.getInstance();
        LoudBalanceChannelConfig.getInstance();
        EventConsumer.getInstance();
        PaymentPostConsumer.getInstance();


        Thread.currentThread().join();
    }
}
