package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.model.PaymentMethod;
import com.payprovider.withdrawal.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payprovider.withdrawal.exception.TransactionException;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Override
    public PaymentMethod findById(Long id) throws TransactionException {
        return paymentMethodRepository.findById(id).orElseThrow(() -> new TransactionException("No payment method found", id));
    }
}
