package com.payprovider.withdrawal.validator;

import com.payprovider.withdrawal.dto.PaymentMethodDto;
import com.payprovider.withdrawal.dto.RequestWithdrawalDto;
import com.payprovider.withdrawal.dto.UserDto;
import com.payprovider.withdrawal.exception.EntityNotExistException;
import com.payprovider.withdrawal.exception.ForbiddenOperationException;
import com.payprovider.withdrawal.service.PaymentMethodService;
import com.payprovider.withdrawal.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.payprovider.withdrawal.service.PaymentMethodService.PAYMENT_METHOD_NOT_FOUND;
import static com.payprovider.withdrawal.service.UserService.USER_NOT_FOUND;
import static com.payprovider.withdrawal.validator.WithdrawalValidator.AMOUNT_MORE_THEN_USER_MAX_WITHDRAWAL_AMOUNT;
import static com.payprovider.withdrawal.validator.WithdrawalValidator.NOT_ALLOWED_PAYMENT_METHOD_FOR_THIS_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class WithdrawalValidatorTest {

    private UserService userService;
    private PaymentMethodService paymentMethodService;
    private WithdrawalValidator withdrawalValidator;

    @BeforeEach
    public void setup() {

        userService = mock(UserService.class);
        paymentMethodService = mock(PaymentMethodService.class);
        withdrawalValidator = new WithdrawalValidator(userService, paymentMethodService);
    }

    @Test
    void shouldThrowErrorIfPaymentMethodNotFound() {

        RequestWithdrawalDto withdrawalDto = RequestWithdrawalDto.builder()
            .paymentMethodId(2L)
            .userId(1L)
            .build();

        given(paymentMethodService.findByIdOrThrow(2L)).willThrow(new EntityNotExistException(PAYMENT_METHOD_NOT_FOUND));

        EntityNotExistException thrown = Assertions.assertThrows(EntityNotExistException.class, () -> {
            withdrawalValidator.validate(withdrawalDto);
        }, "EntityNotExistException was expected");

        assertEquals(PAYMENT_METHOD_NOT_FOUND, thrown.getMessage());
    }

    @Test
    void shouldThrowErrorIfUserNotFound() {

        RequestWithdrawalDto withdrawalDto = RequestWithdrawalDto.builder()
            .paymentMethodId(2L)
            .userId(1L)
            .build();

        given(userService.findByIdOrThrow(1L)).willThrow(new EntityNotExistException(USER_NOT_FOUND));

        EntityNotExistException thrown = Assertions.assertThrows(EntityNotExistException.class, () -> {
            withdrawalValidator.validate(withdrawalDto);
        }, "EntityNotExistException was expected");

        assertEquals(USER_NOT_FOUND, thrown.getMessage());
    }

    @Test
    void shouldThrowErrorIfPaymentMethodNotAllowedForUser() {

        RequestWithdrawalDto withdrawalDto = RequestWithdrawalDto.builder()
            .paymentMethodId(2L)
            .userId(1L)
            .build();
        PaymentMethodDto paymentMethod = PaymentMethodDto.builder()
            .id(3L)
            .name("testMethod")
            .build();
        UserDto user = UserDto.builder()
            .id(1L)
            .paymentMethods(List.of(paymentMethod))
            .build();
        given(userService.findByIdOrThrow(1L)).willReturn(user);

        EntityNotExistException thrown = Assertions.assertThrows(EntityNotExistException.class, () -> {
            withdrawalValidator.validate(withdrawalDto);
        }, "EntityNotExistException was expected");

        assertEquals(NOT_ALLOWED_PAYMENT_METHOD_FOR_THIS_USER, thrown.getMessage());
    }

    @Test
    void shouldThrowErrorIfAmountMoreThanUserMaxAmount() {

        RequestWithdrawalDto withdrawalDto = RequestWithdrawalDto.builder()
            .paymentMethodId(2L)
            .userId(1L)
            .amount(new BigDecimal(100))
            .build();
        PaymentMethodDto paymentMethod = PaymentMethodDto.builder()
            .id(2L)
            .name("testMethod")
            .build();
        UserDto user = UserDto.builder()
            .id(1L)
            .paymentMethods(List.of(paymentMethod))
            .maxWithdrawalAmount(new BigDecimal(101))
            .build();
        given(userService.findByIdOrThrow(1L)).willReturn(user);

        ForbiddenOperationException thrown = Assertions.assertThrows(ForbiddenOperationException.class, () -> {
            withdrawalValidator.validate(withdrawalDto);
        }, "ForbiddenOperationException was expected");

        assertEquals(AMOUNT_MORE_THEN_USER_MAX_WITHDRAWAL_AMOUNT, thrown.getMessage());
    }

    @Test
    void shouldPassValidation() {

        RequestWithdrawalDto withdrawalDto = RequestWithdrawalDto.builder()
            .paymentMethodId(2L)
            .userId(1L)
            .amount(new BigDecimal(100))
            .build();
        PaymentMethodDto paymentMethod = PaymentMethodDto.builder()
            .id(2L)
            .name("testMethod")
            .build();
        UserDto user = UserDto.builder()
            .id(1L)
            .paymentMethods(List.of(paymentMethod))
            .maxWithdrawalAmount(new BigDecimal(100))
            .build();
        given(userService.findByIdOrThrow(1L)).willReturn(user);

        withdrawalValidator.validate(withdrawalDto);
    }
}
