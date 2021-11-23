package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.exception.TransactionException;
import com.payprovider.withdrawal.model.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WithdrawalProcessingService {

    @Autowired
    private RestTemplate restTemplate;

    public Long sendToProcessing(Double amount, PaymentMethod paymentMethod) throws TransactionException {
        // call a payment provider
        // in case a transaction can be process
        // it generates a transactionId and process the transaction async
        // otherwise it throws TransactionException
        return System.nanoTime();
    }
}
