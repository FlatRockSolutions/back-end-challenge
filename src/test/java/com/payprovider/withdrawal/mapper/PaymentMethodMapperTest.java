package com.payprovider.withdrawal.mapper;

import com.payprovider.withdrawal.dto.PaymentMethodDto;
import com.payprovider.withdrawal.model.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PaymentMethodMapper.class)
class PaymentMethodMapperTest {

    @Autowired
    private PaymentMethodMapper paymentMethodMapper;

    @Test
    void shouldMapPaymentMethodToPaymentMethodDto() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(2L);
        paymentMethod.setName("testMethod");

        PaymentMethodDto paymentMethodDto = paymentMethodMapper.paymentMethodToPaymentMethodDto(paymentMethod);

        assertEquals(2L, paymentMethodDto.getId());
        assertEquals("testMethod", paymentMethodDto.getName());
    }

    @Test
    void shouldMapPaymentMethodToPaymentMethodDtoList() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(1L);
        paymentMethod.setName("testMethod");
        PaymentMethod paymentMethod2 = new PaymentMethod();
        paymentMethod2.setId(2L);
        paymentMethod2.setName("testMethod2");

        List<PaymentMethodDto> result = paymentMethodMapper.paymentMethodToPaymentMethodDtoList(List.of(paymentMethod, paymentMethod2));

        assertEquals(2, result.size());
        PaymentMethodDto resultDto = result.get(0);
        assertEquals(1L, resultDto.getId());
        assertEquals("testMethod", resultDto.getName());
        resultDto = result.get(1);
        assertEquals(2L, resultDto.getId());
        assertEquals("testMethod2", resultDto.getName());
    }
}
