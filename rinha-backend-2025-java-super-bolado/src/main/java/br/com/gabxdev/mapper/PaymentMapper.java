package br.com.gabxdev.mapper;

import br.com.gabxdev.model.Payment;

import static br.com.gabxdev.mapper.JsonParse.buildPaymentDTO;

public class PaymentMapper {
    public static Payment toPayment(String payload) {
        var paymentPostToProcessorRequest = new Payment(System.currentTimeMillis(),
                PaymentRequestParse.extractAmountFromRequest(payload));

        paymentPostToProcessorRequest.json = buildPaymentDTO(PaymentRequestParse.extractUUIDFromRequest(payload),
                paymentPostToProcessorRequest);

        return paymentPostToProcessorRequest;
    }
}
