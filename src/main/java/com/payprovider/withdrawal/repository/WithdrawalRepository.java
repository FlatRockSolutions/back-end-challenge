package com.payprovider.withdrawal.repository;

import com.payprovider.withdrawal.model.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
}
