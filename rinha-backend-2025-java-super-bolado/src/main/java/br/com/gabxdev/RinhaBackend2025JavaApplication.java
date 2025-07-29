package br.com.gabxdev;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;
import br.com.gabxdev.router.ApiRouter;
import br.com.gabxdev.worker.PaymentWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RinhaBackend2025JavaApplication {
    public static void main(String[] args) throws InterruptedException {
        var useServer = ApplicationProperties.getInstance().getProperty(PropertiesKey.USER_SERVER);

        ApiRouter.getInstance();
        PaymentWorker.getInstance();

        if (Boolean.parseBoolean(useServer)) {
            SpringApplication.run(RinhaBackend2025JavaApplication.class, args);
        } else {
            Thread.currentThread().join();
        }
    }
}
