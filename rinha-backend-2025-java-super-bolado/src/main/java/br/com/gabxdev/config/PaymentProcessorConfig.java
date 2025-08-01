package br.com.gabxdev.config;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;

import java.net.URI;
import java.time.Duration;

public final class PaymentProcessorConfig {
    private final static PaymentProcessorConfig INSTANCE = new PaymentProcessorConfig();

    private final URI uriProcessorDefault;

    private final URI uriProcessorFallback;

    private final Duration timeoutDefault;

    private PaymentProcessorConfig() {
        var properties = ApplicationProperties.getInstance();

        this.uriProcessorDefault = URI.create(properties.getProperty(PropertiesKey.URL_PROCESSOR_DEFAULT));

        this.uriProcessorFallback = URI.create(properties.getProperty(PropertiesKey.URL_PROCESSOR_FALLBACK));

        var value = Integer.parseInt(properties.getProperty(PropertiesKey.TIMEOUT_PROFESSOR_DEFAULT));

        this.timeoutDefault = Duration.ofMillis(value);
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

    public Duration getTimeoutDefault() {
        return timeoutDefault;
    }
}
