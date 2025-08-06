package br.com.gabxdev.config;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public final class LoudBalanceHostConfig {

    private final static LoudBalanceHostConfig INSTANCE = new LoudBalanceHostConfig();

    private AFUNIXSocketAddress getLbAddress;

    private LoudBalanceHostConfig() {
        try {
            getLbAddress = new AFUNIXSocketAddress(new File("/tmp/loudbalance.sock"));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static LoudBalanceHostConfig getInstance() {
        return INSTANCE;
    }

    public AFUNIXSocketAddress getGetLbAddress() {
        return getLbAddress;
    }
}
