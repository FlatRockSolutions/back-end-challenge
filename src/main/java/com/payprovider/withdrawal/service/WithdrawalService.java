package com.payprovider.withdrawal.service;


import com.payprovider.withdrawal.model.Withdrawal;
import com.payprovider.withdrawal.model.WithdrawalScheduled;

public interface WithdrawalService {

    void create(Withdrawal withdrawal);

    void schedule(WithdrawalScheduled withdrawalScheduled);

    void run();

}
