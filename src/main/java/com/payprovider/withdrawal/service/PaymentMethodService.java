package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.dto.PaymentMethodDto;
import com.payprovider.withdrawal.exception.EntityNotExistException;
import com.payprovider.withdrawal.mapper.PaymentMethodMapper;
import com.payprovider.withdrawal.repository.PaymentMethodRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentMethodService {

    public static final String PAYMENT_METHOD_NOT_FOUND = "Payment method not found";
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;

    /**
     * Return PaymentMethodDto if exist with paymentMethodId ot throw EntityNotExistException.
     *
     * @param paymentMethodId
     * @return PaymentMethodDto if exist with paymentMethodId
     */
    public PaymentMethodDto findByIdOrThrow(Long paymentMethodId) {
        return paymentMethodMapper.paymentMethodToPaymentMethodDto(paymentMethodRepository.findById(paymentMethodId)
            .orElseThrow(() -> new EntityNotExistException(PAYMENT_METHOD_NOT_FOUND)));
    }
}
