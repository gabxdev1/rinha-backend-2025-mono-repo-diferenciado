package br.com.gabxdev.mapper;

import br.com.gabxdev.model.Payment;
import br.com.gabxdev.repository.Amount;

public class PaymentMapper {
    public static Payment toPayment(byte[] payload) {
        var paymentPostToProcessorRequest = new Payment(System.currentTimeMillis(), payload);

        Amount.saveAmount(payload);

        return paymentPostToProcessorRequest;
    }
}
