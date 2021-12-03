package com.payprovider.withdrawal.provider;

import com.payprovider.withdrawal.dto.PaymentMethodDto;
import com.payprovider.withdrawal.exception.TransactionException;
import com.payprovider.withdrawal.model.WithdrawalStatus;
import com.payprovider.withdrawal.service.WithdrawalService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentProvider {

    private final WithdrawalService withdrawalService;

    public PaymentProvider(@Lazy WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    public synchronized Long initTransaction(BigDecimal amount, PaymentMethodDto paymentMethod, Long userId) throws TransactionException {
        //some validation can be added if enough money etc. may also add block for transaction amount.
        return System.nanoTime();
    }

    @Async
    public void proceedTransaction(Long transactionId) {
        //proceed transaction
        withdrawalService.changeWithdrawalStatusByTransactionId(transactionId, WithdrawalStatus.SUCCESS);
    }
}
