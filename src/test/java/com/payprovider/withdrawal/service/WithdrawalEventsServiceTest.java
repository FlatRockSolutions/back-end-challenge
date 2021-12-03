package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.dto.WithdrawalDto;
import com.payprovider.withdrawal.mapper.WithdrawalMapper;
import com.payprovider.withdrawal.model.WithdrawalEvent;
import com.payprovider.withdrawal.model.WithdrawalEventStatus;
import com.payprovider.withdrawal.model.WithdrawalStatus;
import com.payprovider.withdrawal.provider.MessagingProvider;
import com.payprovider.withdrawal.repository.WithdrawalEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WithdrawalEventsServiceTest {

    private WithdrawalEventsService eventsService;
    private WithdrawalEventRepository withdrawalEventRepository;
    private MessagingProvider messagingProvider;
    private WithdrawalMapper withdrawalMapper;

    @BeforeEach
    public void setup() {
        withdrawalEventRepository = mock(WithdrawalEventRepository.class);
        messagingProvider = mock(MessagingProvider.class);
        withdrawalMapper = mock(WithdrawalMapper.class);
        eventsService = new WithdrawalEventsService(withdrawalEventRepository, messagingProvider, withdrawalMapper);
    }

    @Test
    void shouldSendEvent() {
        WithdrawalDto withdrawalDto = WithdrawalDto.builder()
            .id(3L)
            .status(WithdrawalStatus.PROCESSING)
            .build();

        eventsService.sendEvent(withdrawalDto);

        ArgumentCaptor<WithdrawalEvent> argument = ArgumentCaptor.forClass(WithdrawalEvent.class);
        verify(withdrawalEventRepository, times(1)).saveAndFlush(argument.capture());
        WithdrawalEvent event = argument.getValue();
        assertEquals(3L, event.getWithdrawal().getId());
        assertEquals(WithdrawalStatus.PROCESSING, event.getWithdrawalStatus());
        assertNotNull(event.getCreateAt());

        argument = ArgumentCaptor.forClass(WithdrawalEvent.class);
        verify(withdrawalEventRepository, times(1)).save(argument.capture());
        event = argument.getValue();
        assertEquals(3L, event.getWithdrawal().getId());
        assertEquals(WithdrawalStatus.PROCESSING, event.getWithdrawalStatus());
        assertEquals(WithdrawalEventStatus.SEND, event.getWithdrawalEventStatus());
        assertNotNull(event.getCreateAt());

        verify(messagingProvider, times(1)).sendMessage(any());
        verify(withdrawalMapper, times(1)).withdrawalToWithdrawalDto(event.getWithdrawal());
    }
}
