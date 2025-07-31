package br.com.gabxdev;

import br.com.gabxdev.router.ApiRouter;
import br.com.gabxdev.server.UndertowServer;
import br.com.gabxdev.worker.PaymentWorker;

public class RinhaBackend2025JavaApplication {
    public static void main(String[] args) throws InterruptedException {
        start();
    }

    private static void start() throws InterruptedException {
        ApiRouter.getInstance();
        PaymentWorker.getInstance();

        UndertowServer.getInstance().start();

        Thread.currentThread().join();
    }
}
