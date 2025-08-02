package br.com.gabxdev.mapper;

import br.com.gabxdev.model.Payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static br.com.gabxdev.mapper.JsonParse.buildPaymentDTO;

public class PaymentMapper {
    public static Payment toPayment(String payload) {
        var amount = PaymentRequestParse.extractAmountFromRequest(payload);
        var uuid = PaymentRequestParse.extractUUIDFromRequest(payload);

        var paymentPostToProcessorRequest = new Payment(amount,
                System.currentTimeMillis());

        paymentPostToProcessorRequest.json = buildPaymentDTO(uuid, paymentPostToProcessorRequest);

        return paymentPostToProcessorRequest;
    }
}
