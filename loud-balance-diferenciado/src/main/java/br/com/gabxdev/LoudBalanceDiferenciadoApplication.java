package br.com.gabxdev;

import br.com.gabxdev.server.UndertowServer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class LoudBalanceDiferenciadoApplication {

    static {
        try (var input = ClassLoader.getSystemResourceAsStream("logging.properties")) {
            if (input != null) {
                LogManager.getLogManager().readConfiguration(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Logger.getLogger("io.undertow").setLevel(Level.OFF);
        Logger.getLogger("org.xnio").setLevel(Level.OFF);
        Logger.getLogger("org.jboss").setLevel(Level.OFF);
        UndertowServer.getInstance().start();
    }

}
