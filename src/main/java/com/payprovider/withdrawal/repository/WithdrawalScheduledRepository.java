package com.payprovider.withdrawal.repository;

import com.payprovider.withdrawal.model.WithdrawalScheduled;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface WithdrawalScheduledRepository extends JpaRepository<WithdrawalScheduled, Long> {

    List<WithdrawalScheduled> findAllByExecuteAtBefore(Instant date);
}
