package br.com.gabxdev;

import br.com.gabxdev.client.PaymentProcessorClient;
import br.com.gabxdev.config.ApiInternalConfig;
import br.com.gabxdev.config.ApiSockerInternalConfig;
import br.com.gabxdev.config.HttpClientConfig;
import br.com.gabxdev.config.PaymentProcessorConfig;
import br.com.gabxdev.consumer.ApiInternalConsumer;
import br.com.gabxdev.middleware.PaymentMiddleware;
import br.com.gabxdev.middleware.PaymentSummaryWaiter;
import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.repository.InMemoryPaymentDatabase;
import br.com.gabxdev.server.UndertowServer;
import br.com.gabxdev.service.PaymentService;
import br.com.gabxdev.worker.PaymentWorker;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RinhaBackend2025JavaApplication {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("org.xnio.Options.JMX", "false");
        Logger.getLogger("io.undertow").setLevel(Level.OFF);
        Logger.getLogger("org.xnio").setLevel(Level.OFF);
        Logger.getLogger("org.jboss").setLevel(Level.OFF);
        start();
    }

    private static void start() throws InterruptedException {
        ApplicationProperties.getInstance();
        ApiInternalConfig.getInstance();
        ApiSockerInternalConfig.getInstance();
        HttpClientConfig.httpClient();
        PaymentProcessorConfig.getInstance();
        InMemoryPaymentDatabase.getInstance();
        PaymentProcessorClient.getInstance();
        PaymentWorker.getInstance();
        PaymentSummaryWaiter.getInstance();
        PaymentMiddleware.getInstance();
        PaymentService.getInstance();
        ApiInternalConsumer.getInstance();


        UndertowServer.getInstance();


    }
}
