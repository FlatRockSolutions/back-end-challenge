package com.payprovider.withdrawal.provider;

import com.payprovider.withdrawal.dto.WithdrawalDto;
import org.springframework.stereotype.Component;

@Component
public class MessagingProvider {
    public void sendMessage(WithdrawalDto withdrawalDto) {
        //send an event in message queue or throw an error
    }
}
