package com.payprovider.withdrawal.mapper;

import com.payprovider.withdrawal.dto.PaymentMethodDto;
import com.payprovider.withdrawal.model.PaymentMethod;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {

    PaymentMethodDto paymentMethodToPaymentMethodDto(PaymentMethod paymentMethod);

    List<PaymentMethodDto> paymentMethodToPaymentMethodDtoList(List<PaymentMethod> user);
}
