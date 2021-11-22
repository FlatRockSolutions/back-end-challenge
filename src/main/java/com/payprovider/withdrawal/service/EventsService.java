package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.model.Withdrawal;
import com.payprovider.withdrawal.model.WithdrawalScheduled;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EventsService {

    @Async
    public void send(Withdrawal withdrawal) {
        // build and send an event in message queue async
    }

    @Async
    public void send(WithdrawalScheduled withdrawal) {
        // build and send an event in message queue async
    }
}
