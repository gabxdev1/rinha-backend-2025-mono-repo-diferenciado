package br.com.gabxdev;

import br.com.gabxdev.consumer.PaymentSummaryConsumer;
import br.com.gabxdev.server.UndertowServer;

import java.util.logging.Level;
import java.util.logging.Logger;


public class LoudBalanceDiferenciadoApplication {

    public static void main(String[] args) {
        System.setProperty("org.xnio.Options.JMX", "false");
        Logger.getLogger("io.undertow").setLevel(Level.OFF);
        Logger.getLogger("org.xnio").setLevel(Level.OFF);
        Logger.getLogger("org.jboss").setLevel(Level.OFF);

        PaymentSummaryConsumer.getInstance();

        UndertowServer.getInstance().start();
    }
}
