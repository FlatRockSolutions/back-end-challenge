package com.payprovider.withdrawal.repository;

import com.payprovider.withdrawal.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
}
