package br.com.gabxdev.config;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;

public final class ApiInternalConfig {

    private final static ApiInternalConfig INSTANCE = new ApiInternalConfig();

    private final String backEndExternalHost;

    private final int backendExternalPort;

    private ApiInternalConfig() {
        var applicationProperties = ApplicationProperties.getInstance();

        this.backEndExternalHost = applicationProperties.getProperty(PropertiesKey.EXTERNAL_UDP_HOST);

        var portS = applicationProperties.getProperty(PropertiesKey.EXTERNAL_UDP_PORT);

        this.backendExternalPort = Integer.parseInt(portS);
    }

    public static ApiInternalConfig getInstance() {
        return INSTANCE;
    }

    public String getBackEndExternalHost() {
        return backEndExternalHost;
    }

    public int getBackendExternalPort() {
        return backendExternalPort;
    }
}
