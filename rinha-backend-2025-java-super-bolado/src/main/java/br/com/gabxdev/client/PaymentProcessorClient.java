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

    @Value("${rinha.payment.processor.url.default}")
    public String paymentProcessorUrlDefault;

    @Value("${rinha.payment.processor.url.fallback}")
    public String paymentProcessorUrlFallBack;

    @Value("${rinha.payment.processor.timeout-default}")
    public int timeoutDefault;

    @Value("${rinha.payment.processor.timeout-fallback}")
    public int timeoutFallback;

    @Value("${rinha.payment.processor.retry-api-default}")
    public int retryApiDefault;

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
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                })
                .timeout(Duration.ofSeconds(timeoutFallback))
                .onErrorReturn(false)
                .block();
    }

    private Boolean callApiDefault(String json) {
        return apiPaymentProcessor.post()
                .uri(paymentProcessorUrlDefault)
                .bodyValue(json)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                })
                .timeout(Duration.ofSeconds(timeoutDefault))
                .onErrorReturn(false)
                .block();
    }

    /**
     * apiPaymentProcessor.post()
     *                 .uri(paymentProcessorUrlDefault)
     *                 .bodyValue(json)
     *                 .retrieve()
     *                 .toBodilessEntity()
     *                 .timeout(Duration.ofSeconds(timeoutDefault))
     *                 .block();
     */
}
