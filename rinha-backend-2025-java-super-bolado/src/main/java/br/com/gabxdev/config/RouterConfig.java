package br.com.gabxdev.config;

import br.com.gabxdev.handler.PaymentSummaryHandler;
import br.com.gabxdev.handler.PurgePaymentHandler;
import br.com.gabxdev.handler.ReceivePaymentHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;

public class RouterConfig {
    private static final RouterConfig INSTANCE = new RouterConfig();

    private final HttpHandler routes;

    private RouterConfig() {
        this.routes = createRoutes();
    }

    public static RouterConfig getInstance() {
        return INSTANCE;
    }

    private HttpHandler createRoutes() {
        return new PathHandler()
                .addExactPath("/payments", new ReceivePaymentHandler())
                .addPrefixPath("/purge-payments", new PurgePaymentHandler())
                .addPrefixPath("/payments-summary", new PaymentSummaryHandler());
    }

    public HttpHandler getRoutes() {
        return routes;
    }
}
