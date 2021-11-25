package com.payprovider.withdrawal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "scheduled_withdrawals")
public class WithdrawalScheduled {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long transactionId;

    private Double amount;

    private Instant createdAt;

    private Instant executeAt;

    private Long userId;

    private Long paymentMethodId;

    @Enumerated(EnumType.STRING)
    private WithdrawalStatus status;

}
