package br.com.gabxdev.client;

import br.com.gabxdev.commons.HttpHeaders;
import br.com.gabxdev.commons.MediaType;
import br.com.gabxdev.config.HttpClientConfig;
import br.com.gabxdev.config.PaymentProcessorConfig;
import br.com.gabxdev.model.Payment;
import br.com.gabxdev.model.enums.PaymentProcessorType;
import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class PaymentProcessorClient {

    private final static PaymentProcessorClient INSTANCE = new PaymentProcessorClient();

    private final PaymentProcessorConfig paymentProcessorConfig = PaymentProcessorConfig.getInstance();

    private final Duration timeout = Duration.ofSeconds(6);

    private final HttpClient httpClient = HttpClientConfig.httpClient();

    public final int retryApiDefault;

    private PaymentProcessorClient() {
        var applicationProperties = ApplicationProperties.getInstance();

        var retryS = applicationProperties.getProperty(PropertiesKey.RETRY_API_DEFAULT);

        retryApiDefault = Integer.parseInt(retryS);
    }

    public static PaymentProcessorClient getInstance() {
        return INSTANCE;
    }

    public boolean sendPayment(Payment request) {

        if (sendPaymentDefaultWithRetry(request.getJson())) {
            request.setType(PaymentProcessorType.DEFAULT);

            return true;
        }

        if (callApiFallBack(request.getJson())) {
            request.setType(PaymentProcessorType.FALLBACK);

            return true;
        }

        return false;
    }

    private boolean sendPaymentDefaultWithRetry(String json) {
        var request = buildRequest(json, paymentProcessorConfig.getUriProcessorDefault(),
                this.timeout);

        for (int i = 1; i <= retryApiDefault; i++) {
            if (sendRequest(request)) {
                return true;
            }
        }

        return false;
    }

    private boolean callApiFallBack(String json) {
        var request = buildRequest(json, paymentProcessorConfig.getUriProcessorFallback(), this.timeout);

        return sendRequest(request);
    }

    private boolean sendRequest(HttpRequest request) {
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private HttpRequest buildRequest(String body, URI uri, Duration timeout) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .timeout(timeout)
                .header(HttpHeaders.CONTENT_TYPE.getValue(), MediaType.APPLICATION_JSON.getValue())
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }
}
