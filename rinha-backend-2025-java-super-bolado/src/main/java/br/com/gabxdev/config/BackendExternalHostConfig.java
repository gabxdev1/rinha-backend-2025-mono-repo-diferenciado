package br.com.gabxdev.config;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;

public final class BackendExternalHostConfig {

    private final static BackendExternalHostConfig INSTANCE = new BackendExternalHostConfig();

    private String backEndExternalHost;

    private int backendExternalPort;

    private BackendExternalHostConfig() {
        var applicationProperties = ApplicationProperties.getInstance();

        this.backEndExternalHost = applicationProperties.getProperty(PropertiesKey.EXTERNAL_UDP_HOST);

        var portS = applicationProperties.getProperty(PropertiesKey.EXTERNAL_UDP_PORT);

        this.backendExternalPort = Integer.parseInt(portS);
    }

    public static BackendExternalHostConfig getInstance() {
        return INSTANCE;
    }

    public String getBackEndExternalHost() {
        return backEndExternalHost;
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
