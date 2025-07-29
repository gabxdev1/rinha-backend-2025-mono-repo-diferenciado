package br.com.gabxdev;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;
import br.com.gabxdev.router.ApiRouter;
import br.com.gabxdev.server.RinhaHttpServer;
import br.com.gabxdev.worker.PaymentWorker;

public class RinhaBackend2025JavaApplication {
    public static void main(String[] args) throws InterruptedException {
        start();
    }

    private static void start() throws InterruptedException {
        var useServer = ApplicationProperties.getInstance().getProperty(PropertiesKey.USER_SERVER);

        ApiRouter.getInstance();
        PaymentWorker.getInstance();

        if (Boolean.parseBoolean(useServer)) {
            RinhaHttpServer.getInstance().start();
        } else {
            Thread.currentThread().join();
        }
    }
}
