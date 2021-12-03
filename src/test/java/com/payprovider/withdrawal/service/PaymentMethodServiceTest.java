package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.dto.PaymentMethodDto;
import com.payprovider.withdrawal.exception.EntityNotExistException;
import com.payprovider.withdrawal.mapper.PaymentMethodMapper;
import com.payprovider.withdrawal.model.PaymentMethod;
import com.payprovider.withdrawal.repository.PaymentMethodRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PaymentMethodServiceTest {

    private PaymentMethodRepository paymentMethodRepository;
    private PaymentMethodService paymentMethodService;
    private PaymentMethodMapper paymentMethodMapper;

    @BeforeEach
    public void setup() {
        paymentMethodRepository = mock(PaymentMethodRepository.class);
        paymentMethodMapper = mock(PaymentMethodMapper.class);
        paymentMethodService = new PaymentMethodService(paymentMethodRepository, paymentMethodMapper);
    }

    @Test
    public void shouldFindPaymentMethodById() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(2L);
        paymentMethod.setName("testMethod");
        PaymentMethodDto paymentMethodDto = PaymentMethodDto.builder()
            .id(2L)
            .name("testMethod")
            .build();

        given(paymentMethodRepository.findById(2L)).willReturn(Optional.of(paymentMethod));
        given(paymentMethodMapper.paymentMethodToPaymentMethodDto(paymentMethod)).willReturn(paymentMethodDto);

        PaymentMethodDto result = paymentMethodService.findByIdOrThrow(2L);

        assertEquals(paymentMethodDto, result);

        verify(paymentMethodRepository, times(1)).findById(2L);
    }

    @Test
    public void shouldThrowEntityNotExistException() {
        given(paymentMethodRepository.findById(2L)).willReturn(Optional.empty());

        EntityNotExistException thrown = Assertions.assertThrows(EntityNotExistException.class, () -> {
            paymentMethodService.findByIdOrThrow(2L);
        }, "EntityNotExistException was expected");

        assertEquals("Payment method not found", thrown.getMessage());
    }
}
