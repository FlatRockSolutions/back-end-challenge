package com.payprovider.withdrawal.dto;

import com.payprovider.withdrawal.model.Withdrawal;
import com.payprovider.withdrawal.model.WithdrawalScheduled;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class WithdrawalDto {

    private List<Withdrawal> withdrawals;

    private List<WithdrawalScheduled> withdrawalScheduled;

}
