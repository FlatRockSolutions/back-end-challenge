package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.exception.TransactionException;
import com.payprovider.withdrawal.model.PaymentMethod;

public interface PaymentMethodService {

    PaymentMethod findById(Long id) throws TransactionException;
}
