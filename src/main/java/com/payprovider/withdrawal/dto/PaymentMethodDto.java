package com.payprovider.withdrawal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentMethodDto {

    @Schema(description = "Unique identifier of the payment method.", example = "1", required = true)
    private Long id;

    @Schema(description = "Name of the payment method.", example = "My bank account", required = true)
    private String name;
}
