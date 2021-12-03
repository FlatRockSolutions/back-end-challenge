package com.payprovider.withdrawal.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@ToString
@Entity(name = "withdrawal_event")
public class WithdrawalEvent {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "withdrawal_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Withdrawal withdrawal;

    @Enumerated(EnumType.STRING)
    @Column(name = "withdrawal_status")
    private WithdrawalStatus withdrawalStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "withdrawal_event_status")
    private WithdrawalEventStatus withdrawalEventStatus;

    private Instant createAt;
}
