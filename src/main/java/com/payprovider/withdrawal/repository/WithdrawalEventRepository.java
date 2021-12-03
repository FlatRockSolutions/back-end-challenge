package com.payprovider.withdrawal.repository;

import com.payprovider.withdrawal.model.WithdrawalEvent;
import com.payprovider.withdrawal.model.WithdrawalEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawalEventRepository extends JpaRepository<WithdrawalEvent, Long> {

    List<WithdrawalEvent> findAllByWithdrawalEventStatus(WithdrawalEventStatus withdrawalEventStatus);
}
