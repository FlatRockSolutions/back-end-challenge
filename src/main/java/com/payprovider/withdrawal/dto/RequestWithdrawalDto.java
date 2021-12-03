package com.payprovider.withdrawal.dto;

import com.payprovider.withdrawal.model.WithdrawalType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
public class RequestWithdrawalDto {

    @Schema(description = "Amount of withdrawal.", example = "100.0", required = true)
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0.0")
    private BigDecimal amount;

    @Schema(description = "Unique identifier of the user.", example = "1", required = true)
    @NotNull(message = "User id is mandatory")
    private Long userId;

    @Schema(description = "Payment method id.", example = "1", required = true)
    @NotNull(message = "Payment method id is mandatory")
    private Long paymentMethodId;

    @Schema(description = "Withdrawal type name.", example = "ASAP", required = true)
    @NotNull(message = "Withdrawal type name is mandatory")
    private WithdrawalType withdrawalType;
}
