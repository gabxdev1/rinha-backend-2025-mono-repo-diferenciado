package br.com.gabxdev;

import br.com.gabxdev.server.UndertowServer;

public class LoudBalanceDiferenciadoApplication {

    public static void main(String[] args) {
        UndertowServer.getInstance().start();
    }

}
