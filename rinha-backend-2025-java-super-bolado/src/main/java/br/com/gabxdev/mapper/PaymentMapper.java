package br.com.gabxdev.mapper;

import br.com.gabxdev.model.Payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static br.com.gabxdev.mapper.JsonParse.buildPaymentDTO;

public class PaymentMapper {
    public static Payment toPayment(String payload) {
        var body = payload.split(" ");

        var paymentPostToProcessorRequest = new Payment(body[0], new BigDecimal(body[1]),
                Instant.now());

        paymentPostToProcessorRequest.json = buildPaymentDTO(paymentPostToProcessorRequest);

        return paymentPostToProcessorRequest;
    }
}
