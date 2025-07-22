package br.com.gabxdev.client;

import br.com.gabxdev.model.Payment;
import br.com.gabxdev.model.enums.PaymentProcessorType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class PaymentProcessorClient {

    private final WebClient apiPaymentProcessor;

    private final Duration timeout = Duration.ofSeconds(30);

    @Value("${rinha.payment.processor.url.default}")
    private String paymentProcessorUrlDefault;

    @Value("${rinha.payment.processor.url.fallback}")
    private String paymentProcessorUrlFallBack;

    @Value("${rinha.payment.processor.retry-api-default}")
    private int retryApiDefault;

    public PaymentProcessorClient(WebClient apiPaymentProcessor) {
        this.apiPaymentProcessor = apiPaymentProcessor;
    }

    public boolean sendPayment(Payment request) {
        for (int i = 1; i <= retryApiDefault; i++) {
            if (callApiDefault(request.getJson())) {
                request.setType(PaymentProcessorType.DEFAULT);

                return true;
            }
        }

        if (callApiFallBack(request.getJson())) {
            request.setType(PaymentProcessorType.FALLBACK);

            return true;
        }

        return false;
    }

    private Boolean callApiFallBack(String json) {
        return apiPaymentProcessor.post()
                .uri(paymentProcessorUrlFallBack)
                .bodyValue(json)
                .exchangeToMono(response -> Mono.just(response.statusCode().is2xxSuccessful()))
                .timeout(timeout)
                .onErrorReturn(false)
                .block();
    }

    private Boolean callApiDefault(String json) {
        return apiPaymentProcessor.post()
                .uri(paymentProcessorUrlDefault)
                .bodyValue(json)
                .exchangeToMono(response -> Mono.just(response.statusCode().is2xxSuccessful()))
                .timeout(timeout)
                .onErrorReturn(false)
                .block();
    }
}
