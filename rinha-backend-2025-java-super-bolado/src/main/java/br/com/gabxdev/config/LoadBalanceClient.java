package br.com.gabxdev.config;

import java.net.InetAddress;

public record LoadBalanceClient(
        InetAddress ip,

        int port
) {
}
