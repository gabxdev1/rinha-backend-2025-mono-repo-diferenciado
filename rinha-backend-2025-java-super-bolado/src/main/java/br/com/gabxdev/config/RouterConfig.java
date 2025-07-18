package br.com.gabxdev.config;

import br.com.gabxdev.handler.PaymentHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> route(PaymentHandler handler) {
        return RouterFunctions.route()
                .POST("/internal/purge-payments", handler::purgePaymentsInternal)
                .GET("/internal/payments-summary", handler::paymentSummaryInternal)
                .build();
    }
}
