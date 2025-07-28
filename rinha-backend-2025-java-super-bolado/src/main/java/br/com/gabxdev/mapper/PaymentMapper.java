package br.com.gabxdev.mapper;

import br.com.gabxdev.model.Payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static br.com.gabxdev.mapper.JsonParse.buildPaymentDTO;

public class PaymentMapper {
    public static Payment toPaymentInternal(String payload) {
        var amount = PaymentRequestParse.extractAmountFromRequest(payload);
        var uuid = PaymentRequestParse.extractUUIDFromRequest(payload);

        var paymentPostToProcessorRequest = new Payment(uuid, new BigDecimal(amount),
                Instant.now());

        paymentPostToProcessorRequest.json = buildPaymentDTO(paymentPostToProcessorRequest);

        return paymentPostToProcessorRequest;
    }

    public static Payment toPaymentExternal(String payload) {
        var body = payload.split(" ");

        var paymentPostToProcessorRequest = new Payment(body[0], new BigDecimal(body[1]),
                Instant.parse(body[2]));

        paymentPostToProcessorRequest.json = buildPaymentDTO(paymentPostToProcessorRequest);

        return paymentPostToProcessorRequest;
    }


}
