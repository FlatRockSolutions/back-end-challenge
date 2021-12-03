package com.payprovider.withdrawal.repository;

import com.payprovider.withdrawal.model.Withdrawal;
import com.payprovider.withdrawal.model.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {

    List<Withdrawal> findAllByExecuteAtBeforeAndStatus(Instant date, WithdrawalStatus withdrawalStatus);
    Withdrawal findByTransactionId(Long transactionId);
}
