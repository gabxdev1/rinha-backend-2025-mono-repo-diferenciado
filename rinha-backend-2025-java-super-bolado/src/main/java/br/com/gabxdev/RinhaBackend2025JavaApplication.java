package br.com.gabxdev;

import br.com.gabxdev.client.PaymentProcessorClient;
import br.com.gabxdev.config.*;
import br.com.gabxdev.handler.PaymentHandler;
import br.com.gabxdev.middleware.PaymentMiddleware;
import br.com.gabxdev.middleware.PaymentSummaryWaiter;
import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.repository.InMemoryPaymentDatabase;
import br.com.gabxdev.router.ApiRouter;
import br.com.gabxdev.router.PaymentRouter;
import br.com.gabxdev.service.PaymentService;
import br.com.gabxdev.worker.PaymentWorker;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class RinhaBackend2025JavaApplication {
    public static void main(String[] args) throws InterruptedException {
        start();
    }

    private static void start() throws InterruptedException {
        ApplicationProperties.getInstance();
        BackendExternalHostConfig.getInstance();
        DatagramSocketConfig.getInstance();
        DatagramSocketExternalConfig.getInstance();
        HttpClientConfig.httpClient();
        PaymentProcessorConfig.getInstance();
        InMemoryPaymentDatabase.getInstance();
        PaymentProcessorClient.getInstance();
        PaymentWorker.getInstance();
        PaymentSummaryWaiter.getInstance();
        PaymentMiddleware.getInstance();
        PaymentService.getInstance();
        PaymentHandler.getInstance();
        ApiRouter.getInstance();
        PaymentRouter.getInstance();

        Thread.currentThread().join();
    }
}
