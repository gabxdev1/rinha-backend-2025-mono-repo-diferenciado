package br.com.gabxdev.mapper;

import br.com.gabxdev.model.Payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static br.com.gabxdev.mapper.JsonParse.buildPaymentDTO;

public class PaymentMapper {
    private final static BigDecimal amount = new BigDecimal("19.90");

    public static Payment toPayment(String uuid) {
        var paymentPostToProcessorRequest = new Payment(uuid, amount,
                Instant.now());

        paymentPostToProcessorRequest.json = buildPaymentDTO(paymentPostToProcessorRequest);

        return paymentPostToProcessorRequest;
    }
}
