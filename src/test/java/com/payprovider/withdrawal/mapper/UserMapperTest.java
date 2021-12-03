package com.payprovider.withdrawal.mapper;

import com.payprovider.withdrawal.dto.PaymentMethodDto;
import com.payprovider.withdrawal.dto.UserDto;
import com.payprovider.withdrawal.model.PaymentMethod;
import com.payprovider.withdrawal.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserMapper.class)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void shouldMapUserToUserDto() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(2L);
        paymentMethod.setName("testMethod");

        User user = new User();
        user.setId(1L);
        user.setFirstName("testName");
        user.setMaxWithdrawalAmount(new BigDecimal("100.0"));
        user.setPaymentMethods(List.of(paymentMethod));

        UserDto result = userMapper.userToUserDto(user);
        assertEquals(1L, result.getId());
        assertEquals("testName", result.getFirstName());
        assertEquals(new BigDecimal("100.0"), result.getMaxWithdrawalAmount());
        assertEquals(1, result.getPaymentMethods().size());

        PaymentMethodDto resultPaymentMethod = result.getPaymentMethods().get(0);
        assertEquals(2L, resultPaymentMethod.getId());
        assertEquals("testMethod", resultPaymentMethod.getName());
    }

    @Test
    void shouldMapUserToUserDtoList() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(3L);
        paymentMethod.setName("testMethod");

        User user = new User();
        user.setId(1L);
        user.setFirstName("testName");
        user.setMaxWithdrawalAmount(new BigDecimal("100.0"));
        user.setPaymentMethods(List.of(paymentMethod));

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("testName2");
        user2.setMaxWithdrawalAmount(new BigDecimal("200.0"));
        user2.setPaymentMethods(List.of(paymentMethod));

        List<UserDto> result = userMapper.userToUserDtoList(List.of(user, user2));
        assertEquals(2, result.size());
        UserDto resultUser = result.get(0);
        assertEquals(1L, resultUser.getId());
        assertEquals("testName", resultUser.getFirstName());
        assertEquals(new BigDecimal("100.0"), resultUser.getMaxWithdrawalAmount());
        assertEquals(1, resultUser.getPaymentMethods().size());

        PaymentMethodDto resultPaymentMethod = resultUser.getPaymentMethods().get(0);
        assertEquals(3L, resultPaymentMethod.getId());
        assertEquals("testMethod", resultPaymentMethod.getName());

        resultUser = result.get(1);
        assertEquals(2L, resultUser.getId());
        assertEquals("testName2", resultUser.getFirstName());
        assertEquals(new BigDecimal("200.0"), resultUser.getMaxWithdrawalAmount());
        assertEquals(1, resultUser.getPaymentMethods().size());

        resultPaymentMethod = resultUser.getPaymentMethods().get(0);
        assertEquals(3L, resultPaymentMethod.getId());
        assertEquals("testMethod", resultPaymentMethod.getName());
    }
}
