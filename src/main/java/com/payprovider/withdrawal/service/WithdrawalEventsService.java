package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.dto.WithdrawalDto;
import com.payprovider.withdrawal.mapper.WithdrawalMapper;
import com.payprovider.withdrawal.model.Withdrawal;
import com.payprovider.withdrawal.model.WithdrawalEvent;
import com.payprovider.withdrawal.model.WithdrawalEventStatus;
import com.payprovider.withdrawal.provider.MessagingProvider;
import com.payprovider.withdrawal.repository.WithdrawalEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor
public class WithdrawalEventsService {

    private final WithdrawalEventRepository withdrawalEventRepository;
    private final MessagingProvider messagingProvider;
    private final WithdrawalMapper withdrawalMapper;

    /**
     * Async method that create event and send it to the messaging provider.
     * Event is saved to db with status PENDING if no error during send message by messaging provider
     * the status is changed to SEND. This mechanism with scheduled processPendingWithdrawalEvents method
     * provide guaranteed delivery for messages.
     *
     * @param withdrawalDto
     */
    @Async
    @Transactional
    public void sendEvent(WithdrawalDto withdrawalDto) {
        WithdrawalEvent withdrawalEvent = new WithdrawalEvent();
        withdrawalEvent.setWithdrawal(new Withdrawal(withdrawalDto.getId()));
        withdrawalEvent.setWithdrawalStatus(withdrawalDto.getStatus());
        withdrawalEvent.setWithdrawalEventStatus(WithdrawalEventStatus.PENDING);
        withdrawalEvent.setCreateAt(Instant.now());
        withdrawalEventRepository.saveAndFlush(withdrawalEvent);
        sendToMessageQueue(withdrawalEvent);
    }

    /**
     * Scheduled method provide guaranteed delivery for messages.
     */
    @Scheduled(fixedDelayString = "${process.pending.withdrawal.event.fixedDelay:60000}")
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void processPendingWithdrawalEvents() {
        withdrawalEventRepository.findAllByWithdrawalEventStatus(WithdrawalEventStatus.PENDING)
            .forEach(this::sendToMessageQueue);
    }

    private void sendToMessageQueue(WithdrawalEvent withdrawalEvent) {
        withdrawalEvent.setWithdrawalEventStatus(WithdrawalEventStatus.SEND);
        messagingProvider.sendMessage(withdrawalMapper.withdrawalToWithdrawalDto(withdrawalEvent.getWithdrawal()));
        withdrawalEventRepository.save(withdrawalEvent);
    }
}
