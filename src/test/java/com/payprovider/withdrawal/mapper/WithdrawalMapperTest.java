package com.payprovider.withdrawal.mapper;

import com.payprovider.withdrawal.dto.RequestWithdrawalDto;
import com.payprovider.withdrawal.dto.WithdrawalDto;
import com.payprovider.withdrawal.model.PaymentMethod;
import com.payprovider.withdrawal.model.User;
import com.payprovider.withdrawal.model.Withdrawal;
import com.payprovider.withdrawal.model.WithdrawalStatus;
import com.payprovider.withdrawal.model.WithdrawalType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@WebMvcTest(WithdrawalMapper.class)
class WithdrawalMapperTest {

    @Autowired
    private WithdrawalMapper withdrawalMapper;

    @Test
    void shouldMapWithdrawalToWithdrawalDto() {
        User user = new User();
        user.setId(1L);
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(2L);
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setWithdrawalType(WithdrawalType.ASAP);
        withdrawal.setAmount(new BigDecimal("100.0"));
        withdrawal.setPaymentMethod(paymentMethod);
        withdrawal.setUser(user);
        withdrawal.setId(3L);
        withdrawal.setExecuteAt(Instant.now());
        withdrawal.setStatus(WithdrawalStatus.PROCESSING);

        WithdrawalDto withdrawalDto = withdrawalMapper.withdrawalToWithdrawalDto(withdrawal);

        assertEquals(WithdrawalType.ASAP, withdrawalDto.getWithdrawalType());
        assertEquals(new BigDecimal("100.0"), withdrawalDto.getAmount());
        assertEquals(2L, withdrawalDto.getPaymentMethodId());
        assertEquals(1L, withdrawalDto.getUserId());
        assertEquals(3L, withdrawalDto.getId());
        assertEquals(withdrawal.getExecuteAt(), withdrawalDto.getExecuteAt());
        assertEquals(WithdrawalStatus.PROCESSING, withdrawalDto.getStatus());
    }

    @Test
    void shouldMapWithdrawalDtoToWithdrawal() {
        RequestWithdrawalDto withdrawalDto = RequestWithdrawalDto.builder()
            .withdrawalType(WithdrawalType.ASAP)
            .amount(new BigDecimal("100.0"))
            .paymentMethodId(2L)
            .userId(1L)
            .build();

        Withdrawal withdrawal = withdrawalMapper.requestWithdrawalDtoToWithdrawal(withdrawalDto);

        assertEquals(WithdrawalType.ASAP, withdrawal.getWithdrawalType());
        assertEquals(new BigDecimal("100.0"), withdrawal.getAmount());
        assertEquals(2L, withdrawal.getPaymentMethod().getId());
        assertEquals(1L, withdrawal.getUser().getId());
        assertEquals(withdrawal.getExecuteAt(), withdrawal.getExecuteAt());
    }

    @Test
    void shouldMapWithdrawalToWithdrawalDtoList() {
        User user = new User();
        user.setId(1L);
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(2L);
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setWithdrawalType(WithdrawalType.ASAP);
        withdrawal.setAmount(new BigDecimal("100.0"));
        withdrawal.setPaymentMethod(paymentMethod);
        withdrawal.setUser(user);
        withdrawal.setId(3L);
        withdrawal.setExecuteAt(Instant.now());
        withdrawal.setStatus(WithdrawalStatus.PROCESSING);

        Withdrawal withdrawal2 = new Withdrawal();
        withdrawal2.setWithdrawalType(WithdrawalType.SCHEDULED);
        withdrawal2.setAmount(new BigDecimal("200.0"));
        withdrawal2.setPaymentMethod(paymentMethod);
        withdrawal2.setUser(user);
        withdrawal2.setId(4L);
        withdrawal2.setExecuteAt(Instant.now());
        withdrawal2.setStatus(WithdrawalStatus.FAILED);

        List<WithdrawalDto> result = withdrawalMapper.withdrawalToWithdrawalDtoList(List.of(withdrawal, withdrawal2));

        assertEquals(2, result.size());
        WithdrawalDto withdrawalDto = result.get(0);
        assertEquals(WithdrawalType.ASAP, withdrawalDto.getWithdrawalType());
        assertEquals(new BigDecimal("100.0"), withdrawalDto.getAmount());
        assertEquals(2L, withdrawalDto.getPaymentMethodId());
        assertEquals(1L, withdrawalDto.getUserId());
        assertEquals(3L, withdrawalDto.getId());
        assertEquals(withdrawal.getExecuteAt(), withdrawalDto.getExecuteAt());
        assertEquals(WithdrawalStatus.PROCESSING, withdrawalDto.getStatus());
        withdrawalDto = result.get(1);
        assertEquals(WithdrawalType.SCHEDULED, withdrawalDto.getWithdrawalType());
        assertEquals(new BigDecimal("200.0"), withdrawalDto.getAmount());
        assertEquals(2L, withdrawalDto.getPaymentMethodId());
        assertEquals(1L, withdrawalDto.getUserId());
        assertEquals(4L, withdrawalDto.getId());
        assertEquals(withdrawal2.getExecuteAt(), withdrawalDto.getExecuteAt());
        assertEquals(WithdrawalStatus.FAILED, withdrawalDto.getStatus());
    }
}
