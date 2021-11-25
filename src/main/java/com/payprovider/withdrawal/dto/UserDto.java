package com.payprovider.withdrawal.dto;

import com.payprovider.withdrawal.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDto {

    private Long id;

    private String firstName;

    private List<PaymentMethod> paymentMethods;

    private Double maxWithdrawalAmount;

}
