package com.payprovider.withdrawal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class UserDto {

    @Schema(description = "Unique identifier of the user.", example = "1", required = true)
    private Long id;

    @Schema(description = "First name of the user.", example = "David", required = true)
    private String firstName;

    @Schema(description = "List of payment methods.", example = "1", required = true)
    private List<PaymentMethodDto> paymentMethods;

    @Schema(description = "Max withdrawal amount for user", example = "1")
    private BigDecimal maxWithdrawalAmount;
}
