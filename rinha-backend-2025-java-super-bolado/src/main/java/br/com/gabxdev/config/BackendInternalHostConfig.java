package br.com.gabxdev.config;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;

public final class BackendInternalHostConfig {

    private final static BackendInternalHostConfig INSTANCE = new BackendInternalHostConfig();

    private String backEndExternalHost;

    private int backendExternalPort;

    private BackendInternalHostConfig() {
        var applicationProperties = ApplicationProperties.getInstance();

        this.backEndExternalHost = applicationProperties.getProperty(PropertiesKey.EXTERNAL_UDP_HOST);

        var portS = applicationProperties.getProperty(PropertiesKey.EXTERNAL_UDP_PORT);


        this.backendExternalPort = Integer.parseInt(portS);
    }

    public static BackendInternalHostConfig getInstance() {
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
