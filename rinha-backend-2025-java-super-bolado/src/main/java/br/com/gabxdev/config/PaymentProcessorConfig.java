package br.com.gabxdev.config;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;

import java.net.URI;

public final class PaymentProcessorConfig {
    private final static PaymentProcessorConfig INSTANCE = new PaymentProcessorConfig();

    private final URI uriProcessorDefault;

    private final URI uriProcessorFallback;

    private PaymentProcessorConfig() {
        var properties = ApplicationProperties.getInstance();

        this.uriProcessorDefault = URI.create(properties.getProperty(PropertiesKey.URL_PROCESSOR_DEFAULT));

        this.uriProcessorFallback = URI.create(properties.getProperty(PropertiesKey.URL_PROCESSOR_FALLBACK));
    }

    public static PaymentProcessorConfig getInstance() {
        return INSTANCE;
    }

    public URI getUriProcessorDefault() {
        return uriProcessorDefault;
    }

    public URI getUriProcessorFallback() {
        return uriProcessorFallback;
    }
}
