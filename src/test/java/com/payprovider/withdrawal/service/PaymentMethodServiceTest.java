package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.Application;
import com.payprovider.withdrawal.exception.TransactionException;
import com.payprovider.withdrawal.model.PaymentMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class
)
@ContextConfiguration
public class PaymentMethodServiceTest {

    private static final Long TEST_PAYMENT_ID = 1L;
    private static final Long INCORRECT_TEST_PAYMENT_ID = 8L;

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Test
    public void testFindById() throws TransactionException {
        PaymentMethod paymentMethod = paymentMethodService.findById(TEST_PAYMENT_ID);
        assertNotNull(paymentMethod);
        assertThat(paymentMethod.getId().equals(1L));
        assertThat(paymentMethod.getName().equals("My bank account"));
        assertThat(paymentMethod.getUser().getFirstName().equals("David"));
    }

    @Test(expected = TransactionException.class)
    public void testPaymentNotFound() throws TransactionException {
        paymentMethodService.findById(INCORRECT_TEST_PAYMENT_ID);
    }
}
