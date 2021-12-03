package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.dto.PaymentMethodDto;
import com.payprovider.withdrawal.exception.TransactionException;
import com.payprovider.withdrawal.provider.PaymentProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WithdrawalProcessingServiceTest {

    private WithdrawalProcessingService withdrawalProcessingService;
    private PaymentProvider paymentProvider;

    @BeforeEach
    public void setup() {
        paymentProvider = mock(PaymentProvider.class);
        withdrawalProcessingService = new WithdrawalProcessingService(paymentProvider);
    }

    @Test
    void shouldSendToProcessingAndGenerateId() throws TransactionException {
        PaymentMethodDto paymentMethodDto = PaymentMethodDto.builder()
            .id(2L)
            .name("testMethod")
            .build();
        BigDecimal amount = new BigDecimal("100.0");
        long userId = 1L;
        long expectedTransactionId = 2L;
        given(paymentProvider.initTransaction(amount, paymentMethodDto, userId)).willReturn(expectedTransactionId);

        Long transactionId = withdrawalProcessingService.sendToProcessing(amount, paymentMethodDto, userId);

        verify(paymentProvider, times(1)).initTransaction(amount, paymentMethodDto, userId);
        verify(paymentProvider, times(1)).proceedTransaction(expectedTransactionId);
        assertEquals(expectedTransactionId, transactionId);
    }
}
