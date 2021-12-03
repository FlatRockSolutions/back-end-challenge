package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.dto.PaymentMethodDto;
import com.payprovider.withdrawal.dto.RequestWithdrawalDto;
import com.payprovider.withdrawal.exception.EntityNotExistException;
import com.payprovider.withdrawal.exception.TransactionException;
import com.payprovider.withdrawal.mapper.WithdrawalMapper;
import com.payprovider.withdrawal.model.PaymentMethod;
import com.payprovider.withdrawal.model.User;
import com.payprovider.withdrawal.model.Withdrawal;
import com.payprovider.withdrawal.model.WithdrawalStatus;
import com.payprovider.withdrawal.model.WithdrawalType;
import com.payprovider.withdrawal.repository.WithdrawalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WithdrawalServiceTest {

    private WithdrawalRepository withdrawalRepository;
    private WithdrawalProcessingService withdrawalProcessingService;
    private PaymentMethodService paymentMethodService;
    private WithdrawalEventsService eventsService;
    private WithdrawalMapper withdrawalMapper;
    private WithdrawalService withdrawalService;

    @BeforeEach
    public void setup() {
        withdrawalRepository = mock(WithdrawalRepository.class);
        withdrawalProcessingService = mock(WithdrawalProcessingService.class);
        paymentMethodService = mock(PaymentMethodService.class);
        eventsService = mock(WithdrawalEventsService.class);
        withdrawalMapper = mock(WithdrawalMapper.class);
        withdrawalService = new WithdrawalService(withdrawalRepository, withdrawalProcessingService, paymentMethodService, eventsService, withdrawalMapper);
    }

    @Test
    public void shouldProcessWithdrawalWithAsapStatus() {
        WithdrawalService withdrawalServiceSpy = spy(withdrawalService);
        RequestWithdrawalDto withdrawalDtoAfterUpdate = RequestWithdrawalDto.builder()
            .withdrawalType(WithdrawalType.ASAP)
            .amount(new BigDecimal("100.0"))
            .paymentMethodId(2L)
            .userId(1L)
            .build();
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        withdrawal.setId(3L);
        withdrawal.setWithdrawalType(WithdrawalType.ASAP);
        given(withdrawalRepository.save(any())).willReturn(withdrawal);
        given(withdrawalMapper.requestWithdrawalDtoToWithdrawal(withdrawalDtoAfterUpdate)).willReturn(withdrawal);
        doNothing().when(withdrawalServiceSpy).processedWithdrawal(withdrawal);

        withdrawalServiceSpy.create(withdrawalDtoAfterUpdate);

        ArgumentCaptor<Withdrawal> argument = ArgumentCaptor.forClass(Withdrawal.class);
        verify(withdrawalRepository, times(1)).save(argument.capture());
        Withdrawal withdrawalToSave = argument.getValue();
        assertEquals(WithdrawalStatus.PENDING, withdrawalToSave.getStatus());
        assertNotNull(withdrawalToSave.getCreatedAt());

        verify(withdrawalServiceSpy, times(1)).processedWithdrawal(withdrawal);
        verify(withdrawalMapper, times(1)).withdrawalToWithdrawalDto(withdrawal);
    }

    @Test
    public void shouldScheduleWithdrawalWithScheduledStatus() {
        WithdrawalService withdrawalServiceSpy = spy(withdrawalService);
        RequestWithdrawalDto withdrawalDtoAfterUpdate = RequestWithdrawalDto.builder()
            .withdrawalType(WithdrawalType.SCHEDULED)
            .amount(new BigDecimal("100.0"))
            .paymentMethodId(2L)
            .userId(1L)
            .build();
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        withdrawal.setId(3L);
        withdrawal.setWithdrawalType(WithdrawalType.SCHEDULED);
        given(withdrawalMapper.requestWithdrawalDtoToWithdrawal(withdrawalDtoAfterUpdate)).willReturn(withdrawal);
        given(withdrawalRepository.save(any())).willReturn(withdrawal);

        withdrawalServiceSpy.create(withdrawalDtoAfterUpdate);

        verify(withdrawalMapper, times(1)).withdrawalToWithdrawalDto(withdrawal);

        ArgumentCaptor<Withdrawal> argument = ArgumentCaptor.forClass(Withdrawal.class);
        verify(withdrawalRepository, times(1)).save(argument.capture());
        Withdrawal withdrawalToSave = argument.getValue();
        assertEquals(WithdrawalStatus.PENDING, withdrawalToSave.getStatus());
        assertNotNull(withdrawalToSave.getCreatedAt());

        verify(withdrawalServiceSpy, times(0)).processedWithdrawal(withdrawal);
    }

    @Test
    public void shouldProcessWithdrawalAndSetPendingStatusAndTransactionId() throws TransactionException {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(1L);
        paymentMethod.setName("some name");
        User user = new User();
        user.setId(1L);
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        withdrawal.setId(3L);
        withdrawal.setPaymentMethod(paymentMethod);
        withdrawal.setAmount(new BigDecimal("111.01"));
        withdrawal.setWithdrawalType(WithdrawalType.SCHEDULED);
        withdrawal.setUser(user);
        PaymentMethodDto paymentMethodDto = PaymentMethodDto.builder()
            .id(1L)
            .name("some name")
            .build();
        Long transactionId = 55L;
        given(paymentMethodService.findByIdOrThrow(1L)).willReturn(paymentMethodDto);
        given(withdrawalProcessingService.sendToProcessing(withdrawal.getAmount(), paymentMethodDto, user.getId())).willReturn(transactionId);

        withdrawalService.processedWithdrawal(withdrawal);

        verify(withdrawalRepository, times(1)).save(withdrawal);
        verify(withdrawalMapper, times(1)).withdrawalToWithdrawalDto(withdrawal);
        verify(eventsService, times(1)).sendEvent(any());

        assertEquals(transactionId, withdrawal.getTransactionId());
        assertEquals(WithdrawalStatus.PROCESSING, withdrawal.getStatus());
    }

    @Test
    public void shouldProcessWithdrawalAndSetFailedStatus() throws TransactionException {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(1L);
        paymentMethod.setName("some name");
        User user = new User();
        user.setId(1L);
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        withdrawal.setId(3L);
        withdrawal.setPaymentMethod(paymentMethod);
        withdrawal.setAmount(new BigDecimal("111.01"));
        withdrawal.setWithdrawalType(WithdrawalType.SCHEDULED);
        withdrawal.setUser(user);
        PaymentMethodDto paymentMethodDto = PaymentMethodDto.builder()
            .id(1L)
            .name("some name")
            .build();
        given(paymentMethodService.findByIdOrThrow(1L)).willReturn(paymentMethodDto);
        given(withdrawalProcessingService.sendToProcessing(withdrawal.getAmount(), paymentMethodDto, user.getId())).willThrow(new TransactionException());

        withdrawalService.processedWithdrawal(withdrawal);

        verify(withdrawalRepository, times(1)).save(withdrawal);
        verify(withdrawalMapper, times(1)).withdrawalToWithdrawalDto(withdrawal);
        verify(eventsService, times(1)).sendEvent(any());

        assertNull(withdrawal.getTransactionId());
        assertEquals(WithdrawalStatus.FAILED, withdrawal.getStatus());
    }

    @Test
    public void shouldProcessWithdrawalAndSetInternalErrorStatus() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(1L);
        paymentMethod.setName("some name");
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        withdrawal.setId(3L);
        withdrawal.setPaymentMethod(paymentMethod);
        withdrawal.setAmount(new BigDecimal("111.01"));
        withdrawal.setWithdrawalType(WithdrawalType.SCHEDULED);

        given(paymentMethodService.findByIdOrThrow(1L)).willThrow(new EntityNotExistException("Payment method not found"));

        withdrawalService.processedWithdrawal(withdrawal);

        verify(withdrawalRepository, times(1)).save(withdrawal);
        verify(withdrawalMapper, times(1)).withdrawalToWithdrawalDto(withdrawal);
        verify(eventsService, times(1)).sendEvent(any());

        assertNull(withdrawal.getTransactionId());
        assertEquals(WithdrawalStatus.INTERNAL_ERROR, withdrawal.getStatus());
    }

    @Test
    public void shouldProcessPendingWithdrawal() {
        WithdrawalService withdrawalServiceSpy = spy(withdrawalService);
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        withdrawal.setId(3L);
        withdrawal.setAmount(new BigDecimal("111.01"));
        withdrawal.setWithdrawalType(WithdrawalType.SCHEDULED);

        given(withdrawalRepository.findAllByExecuteAtBeforeAndStatus(any(), eq(WithdrawalStatus.PENDING))).willReturn(List.of(withdrawal));
        doNothing().when(withdrawalServiceSpy).processedWithdrawal(any());

        withdrawalServiceSpy.processPendingWithdrawal();

        verify(withdrawalServiceSpy, times(1)).processedWithdrawal(withdrawal);
    }

    @Test
    public void shouldFindAllWithdrawal() {
        withdrawalService.findAll();

        verify(withdrawalRepository, times(1)).findAll();
        verify(withdrawalMapper, times(1)).withdrawalToWithdrawalDtoList(any());
    }

    @Test
    void shouldChangeWithdrawalStatusByTransactionId() {
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        withdrawal.setId(3L);
        withdrawal.setAmount(new BigDecimal("111.01"));
        withdrawal.setWithdrawalType(WithdrawalType.SCHEDULED);
        given(withdrawalRepository.findByTransactionId(1L)).willReturn(withdrawal);

        withdrawalService.changeWithdrawalStatusByTransactionId(1L, WithdrawalStatus.SUCCESS);

        verify(withdrawalRepository, times(1)).findByTransactionId(1L);
        verify(withdrawalRepository, times(1)).save(withdrawal);
    }
}
