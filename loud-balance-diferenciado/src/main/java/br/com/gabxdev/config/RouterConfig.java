package br.com.gabxdev.config;

import br.com.gabxdev.handler.LoadBalanceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> route(LoadBalanceHandler handler) {
        return RouterFunctions.route()
                .POST("/payments", handler::receivePayment)
                .POST("/purge-payments", handler::purgePayments)
                .GET("/payments-summary", handler::paymentSummary)
                .build();
    }
}
