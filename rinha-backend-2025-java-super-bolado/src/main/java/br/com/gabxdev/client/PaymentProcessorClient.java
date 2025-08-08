package br.com.gabxdev.client;

import br.com.gabxdev.commons.HttpHeaders;
import br.com.gabxdev.commons.MediaType;
import br.com.gabxdev.config.HttpClientConfig;
import br.com.gabxdev.config.PaymentProcessorConfig;
import br.com.gabxdev.mapper.JsonParse;
import br.com.gabxdev.model.Payment;
import br.com.gabxdev.model.enums.PaymentProcessorType;
import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.locks.LockSupport;

public final class PaymentProcessorClient {

    private final static PaymentProcessorClient INSTANCE = new PaymentProcessorClient();

    private final PaymentProcessorConfig paymentProcessorConfig = PaymentProcessorConfig.getInstance();

    private final Duration timeout = Duration.ofSeconds(6);

    private final HttpClient httpClient = HttpClientConfig.httpClient();

    private final long backoff;

    public final int retryApiDefault;

    private PaymentProcessorClient() {
        var properties = ApplicationProperties.getInstance();

        var retryS = properties.getProperty(PropertiesKey.RETRY_API_DEFAULT);
        backoff = Long.parseLong(properties.getProperty(PropertiesKey.CLIENT_BACKOFF)) * 1_000_000L;

        retryApiDefault = Integer.parseInt(retryS);
    }

    public static PaymentProcessorClient getInstance() {
        return INSTANCE;
    }

    public boolean sendPayment(Payment request) {
        buildPaymentDtoRequest(request);

        if (sendPaymentDefaultWithRetry(request.getJson())) {
            request.setType(PaymentProcessorType.DEFAULT);

            return true;
        }

//        if (callApiFallBack(request.getJson())) {
//            request.setType(PaymentProcessorType.FALLBACK);
//
//            return true;
//        }

        return false;
    }

    private void buildPaymentDtoRequest(Payment request) {
        if (request.getJson() == null) {
            request.setJson(JsonParse.buildPaymentDTO(request.getPayload(), request.getRequestedAt()));
        }
    }

    private boolean sendPaymentDefaultWithRetry(byte[] json) {
        var request = buildRequest(json, paymentProcessorConfig.getUriProcessorDefault(),
                this.timeout);

        for (int i = 1; i <= retryApiDefault; i++) {
            if (sendRequest(request)) {
                return true;
            }

            LockSupport.parkNanos(backoff);
        }

        return false;
    }

    private boolean callApiFallBack(byte[] json) {
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

    private HttpRequest buildRequest(byte[] body, URI uri, Duration timeout) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .timeout(timeout)
                .header(HttpHeaders.CONTENT_TYPE.getValue(), MediaType.APPLICATION_JSON.getValue())
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();
    }
}
