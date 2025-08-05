package br.com.gabxdev.config;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class LoudBalanceHostConfig {

    private final static LoudBalanceHostConfig INSTANCE = new LoudBalanceHostConfig();

    private String backEndExternalHost;

    private int backendExternalPort;

    private LoudBalanceHostConfig() {
        var applicationProperties = ApplicationProperties.getInstance();

        this.backEndExternalHost = applicationProperties.getProperty(PropertiesKey.LB_URL);

        this.backendExternalPort = 9096;
    }

    public static LoudBalanceHostConfig getInstance() {
        return INSTANCE;
    }

    public InetAddress getBackEndExternalHost() {
        try {
            return InetAddress.getByName(backEndExternalHost);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBackEndExternalHost(String backEndExternalHost) {
        this.backEndExternalHost = backEndExternalHost;
    }

    public int getBackendExternalPort() {
        return backendExternalPort;
    }

    public void setBackendExternalPort(int backendExternalPort) {
        this.backendExternalPort = backendExternalPort;
    }
}
