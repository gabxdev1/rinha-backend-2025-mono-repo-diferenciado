package br.com.gabxdev.config;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;
import br.com.gabxdev.socket.BackendAddress;

import java.util.List;

public class BackendUrlConfig {

    private final static BackendUrlConfig INSTANCE = new BackendUrlConfig();

    private final String backEndUrl1;

    private final int backEndPort1;

    private final String backEndUrl2;

    private final int backEndPort2;

    private List<BackendAddress> backendAddresses;

    public static BackendUrlConfig getInstance() {
        return INSTANCE;
    }

    private BackendUrlConfig() {
        var properties = ApplicationProperties.getInstance();

        backEndUrl1 = properties.getProperty(PropertiesKey.BACK_END_1_URL);
        backEndPort1 = Integer.parseInt(properties.getProperty(PropertiesKey.BACK_END_1_PORT));

        backEndUrl2 = properties.getProperty(PropertiesKey.BACK_END_2_URL);
        backEndPort2 = Integer.parseInt(properties.getProperty(PropertiesKey.BACK_END_2_PORT));

        start();
    }

    private void start() {
        var socketAddress1 = new BackendAddress(backEndUrl1, backEndPort1);
        var socketAddress2 = new BackendAddress(backEndUrl2, backEndPort2);

        this.backendAddresses = java.util.List.of(socketAddress1, socketAddress2);
    }

    public List<BackendAddress> getBackendsAddresses() {
        return this.backendAddresses;
    }
}
