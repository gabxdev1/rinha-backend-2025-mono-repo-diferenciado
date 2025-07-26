package br.com.gabxdev.client;

import br.com.gabxdev.model.Payment;
import br.com.gabxdev.model.enums.PaymentProcessorType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class PaymentProcessorClient {
    private final HttpClient paymentClient;

    private final URI uriDefault;

    private final URI uriFallback;

    private final Duration timeoutApiDefault;

    private final Duration timeoutApiFallback;

    @Value("${rinha.payment.processor.retry-api-default}")
    public int retryApiDefault;

    @Value("${rinha.payment.processor.back-off-api-default}")
    private int backOfRequestDefault;

    public PaymentProcessorClient(HttpClient paymentClient, URI uriDefault, Duration timeoutApiDefault, Duration timeoutApiFallback, URI uriFallback) {
        this.paymentClient = paymentClient;
        this.uriDefault = uriDefault;
        this.timeoutApiDefault = timeoutApiDefault;
        this.timeoutApiFallback = timeoutApiFallback;
        this.uriFallback = uriFallback;
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
        var request = buildRequest(json, uriDefault, timeoutApiDefault);

        for (int i = 1; i <= retryApiDefault; i++) {
            if (sendRequest(request)) {
                return true;
            }

            backOff();
        }

        return false;
    }

    private void backOff() {
        try {
            Thread.sleep(backOfRequestDefault);
        } catch (InterruptedException ignored) {
        }
    }

    private boolean callApiFallBack(String json) {
        var request = buildRequest(json, uriFallback, timeoutApiFallback);

        return sendRequest(request);
    }

    private boolean sendRequest(HttpRequest request) {
        try {
            var response = paymentClient.send(request, HttpResponse.BodyHandlers.discarding());

            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private HttpRequest buildRequest(String body, URI uri, Duration timeout) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .timeout(timeout)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }
}
