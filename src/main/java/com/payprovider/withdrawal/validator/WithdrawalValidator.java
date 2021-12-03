package com.payprovider.withdrawal.validator;

import com.payprovider.withdrawal.dto.RequestWithdrawalDto;
import com.payprovider.withdrawal.dto.UserDto;
import com.payprovider.withdrawal.exception.EntityNotExistException;
import com.payprovider.withdrawal.exception.ForbiddenOperationException;
import com.payprovider.withdrawal.service.PaymentMethodService;
import com.payprovider.withdrawal.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
@AllArgsConstructor
public class WithdrawalValidator {

    public static final String NOT_ALLOWED_PAYMENT_METHOD_FOR_THIS_USER = "Not allowed payment method for this user";
    public static final String AMOUNT_MORE_THEN_USER_MAX_WITHDRAWAL_AMOUNT = "The withdrawal amount more then user max withdrawal amount.";
    private final UserService userService;
    private final PaymentMethodService paymentMethodService;

    /**
     * Validate request for withdrawal creation.
     * Validate that payment method exists.
     * Validate that user exists.
     * Validate that user has the payment method.
     * Validate that the user max withdrawal amount more or equal to the requested withdrawal.
     *
     * @param withdrawalDto
     */
    public void validate(RequestWithdrawalDto withdrawalDto) {
        paymentMethodService.findByIdOrThrow(withdrawalDto.getPaymentMethodId());
        UserDto user = userService.findByIdOrThrow(withdrawalDto.getUserId());
        checkPaymentMethodForUser(withdrawalDto.getPaymentMethodId(), user);
        checkAmountForUser(withdrawalDto.getAmount(), user);
    }

    private void checkAmountForUser(BigDecimal withdrawalAmount, UserDto user) {
        if (withdrawalAmount.compareTo(user.getMaxWithdrawalAmount()) < 0) {
            throw new ForbiddenOperationException(AMOUNT_MORE_THEN_USER_MAX_WITHDRAWAL_AMOUNT);
        }
    }

    private void checkPaymentMethodForUser(Long paymentMethodId, UserDto user) {
        boolean isExistForUser = user.getPaymentMethods().stream()
            .anyMatch(p -> p.getId().equals(paymentMethodId));
        if (!isExistForUser) {
            throw new EntityNotExistException(NOT_ALLOWED_PAYMENT_METHOD_FOR_THIS_USER);
        }
    }
}
