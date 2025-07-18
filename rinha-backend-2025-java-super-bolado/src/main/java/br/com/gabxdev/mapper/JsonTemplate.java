package br.com.gabxdev.mapper;

public class JsonTemplate {

    public final static String PAYMENT_POST_REQUEST =
            """
                    {
                        "correlationId": "%s",
                        "amount": %s,
                        "requestedAt" : "%s"
                    }
                    """;

    public final static String PAYMENT_SUMMARY =
            """
                    {
                        "default": {
                            "totalRequests": %s,
                            "totalAmount": %s
                        },
                        "fallback": {
                            "totalRequests": %s,
                            "totalAmount": %s
                        }
                    }
                    """;
}
