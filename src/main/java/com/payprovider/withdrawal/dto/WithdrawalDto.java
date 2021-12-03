package com.payprovider.withdrawal.dto;

import com.payprovider.withdrawal.model.WithdrawalStatus;
import com.payprovider.withdrawal.model.WithdrawalType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalDto {

    @Schema(description = "Unique identifier of the withdrawal.", example = "1", required = true)
    private Long id;

    @Schema(description = "Unique identifier of the transaction.", example = "1", required = true)
    private Long transactionId;

    @Schema(description = "Amount of withdrawal.", example = "100.0", required = true)
    private BigDecimal amount;

    @Schema(description = "Withdrawal creating time.", example = "2021-12-03T13:05:52.303Z", required = true)
    private Instant createdAt;

    @Schema(description = "Unique identifier of the user.", example = "1", required = true)
    private Long userId;

    @Schema(description = "Payment method id.", example = "1", required = true)
    private Long paymentMethodId;

    @Schema(description = "Withdrawal executing time.", example = "2021-12-03T13:05:52.303Z", required = true)
    private Instant executeAt;

    @Schema(description = "Withdrawal status.", example = "PENDING", required = true)
    private WithdrawalStatus status;

    @Schema(description = "Withdrawal type name.", example = "ASAP", required = true)
    private WithdrawalType withdrawalType;
}
