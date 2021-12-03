package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.dto.PaymentMethodDto;
import com.payprovider.withdrawal.exception.TransactionException;
import com.payprovider.withdrawal.provider.PaymentProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@AllArgsConstructor
public class WithdrawalProcessingService {

    private final PaymentProvider paymentProvider;

    /**
     * Send withdrawal to processing. The sending do in two steps:
     * 1. Initialize transaction and get transaction id if no error exists.
     * 2. Proceed transaction.
     *
     * @param amount
     * @param paymentMethod
     * @param userId
     * @return transaction id
     * @throws TransactionException if transaction can't be init
     */
    public Long sendToProcessing(BigDecimal amount, PaymentMethodDto paymentMethod, Long userId) throws TransactionException {
        Long transactionId = paymentProvider.initTransaction(amount, paymentMethod, userId);
        paymentProvider.proceedTransaction(transactionId);
        return transactionId;
    }
}
