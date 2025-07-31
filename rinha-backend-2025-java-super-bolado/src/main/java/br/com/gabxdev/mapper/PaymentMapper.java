package br.com.gabxdev.mapper;

import br.com.gabxdev.model.Payment;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static br.com.gabxdev.mapper.JsonParse.buildPaymentDTO;

public class PaymentMapper {
    public static Payment toPaymentInternal(byte[] payload) {
        var amount = PaymentRequestParse.extractAmountFromRequest(new String(payload, StandardCharsets.UTF_8));
        var uuid = PaymentRequestParse.extractUUIDFromRequest(new String(payload, StandardCharsets.UTF_8));

        var paymentPostToProcessorRequest = new Payment(uuid, amount,
                System.currentTimeMillis());

        paymentPostToProcessorRequest.json = buildPaymentDTO(paymentPostToProcessorRequest);

        return paymentPostToProcessorRequest;
    }
}
