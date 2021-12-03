package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.dto.PaymentMethodDto;
import com.payprovider.withdrawal.dto.RequestWithdrawalDto;
import com.payprovider.withdrawal.dto.WithdrawalDto;
import com.payprovider.withdrawal.mapper.WithdrawalMapper;
import com.payprovider.withdrawal.model.WithdrawalStatus;
import com.payprovider.withdrawal.model.WithdrawalType;
import com.payprovider.withdrawal.repository.WithdrawalRepository;
import com.payprovider.withdrawal.exception.TransactionException;
import com.payprovider.withdrawal.model.Withdrawal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


@Service
@Slf4j
@AllArgsConstructor
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final WithdrawalProcessingService withdrawalProcessingService;
    private final PaymentMethodService paymentMethodService;
    private final WithdrawalEventsService eventsService;
    private final WithdrawalMapper withdrawalMapper;

    /**
     * Create withdrawal. If Withdrawal Type is ASAP the withdrawal send to proceed otherwise save to db
     * and waiting to executeAt the time to proceed in processPendingWithdrawal method.
     *
     * @param requestWithdrawalDto withdrawal to be processed
     * @return withdrawal after processed
     */
    public WithdrawalDto create(RequestWithdrawalDto requestWithdrawalDto) {
        Withdrawal withdrawal = withdrawalMapper.requestWithdrawalDtoToWithdrawal(requestWithdrawalDto);
        withdrawal.setCreatedAt(Instant.now());
        withdrawal.setStatus(WithdrawalStatus.PENDING);

        withdrawal = withdrawalRepository.save(withdrawal);
        if (withdrawal.getWithdrawalType() == WithdrawalType.ASAP) {
            processedWithdrawal(withdrawal);
        }
        return withdrawalMapper.withdrawalToWithdrawalDto(withdrawal);
    }

    /**
     * Processed withdrawal. Send withdrawal to processing service and event after init transaction.
     *
     * @param withdrawal
     */
    public synchronized void processedWithdrawal(Withdrawal withdrawal) {
        log.info("Process withdrawal: " + withdrawal);
        try {
            PaymentMethodDto paymentMethod = paymentMethodService.findByIdOrThrow(withdrawal.getPaymentMethod().getId());
            var transactionId = withdrawalProcessingService.sendToProcessing(withdrawal.getAmount(), paymentMethod, withdrawal.getUser().getId());
            withdrawal.setStatus(WithdrawalStatus.PROCESSING);
            withdrawal.setTransactionId(transactionId);
        } catch (TransactionException e) {
            log.error(e.getMessage(), e);
            withdrawal.setStatus(WithdrawalStatus.FAILED);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            withdrawal.setStatus(WithdrawalStatus.INTERNAL_ERROR);
        }
        withdrawalRepository.save(withdrawal);
        eventsService.sendEvent(withdrawalMapper.withdrawalToWithdrawalDto(withdrawal));
    }

    /**
     * Process pending withdrawal.
     * property process.pending.withdrawal.fixedDelay can be used to change fixedDelay.
     */
    @Scheduled(fixedDelayString = "${process.pending.withdrawal.fixedDelay:5000}")
    public void processPendingWithdrawal() {
        withdrawalRepository.findAllByExecuteAtBeforeAndStatus(Instant.now(), WithdrawalStatus.PENDING)
            .forEach(this::processedWithdrawal);
    }

    public List<WithdrawalDto> findAll() {
        return withdrawalMapper.withdrawalToWithdrawalDtoList(withdrawalRepository.findAll());
    }

    public synchronized void changeWithdrawalStatusByTransactionId(Long transactionId, WithdrawalStatus withdrawalStatus) {
        Withdrawal  withdrawal = withdrawalRepository.findByTransactionId(transactionId);
        withdrawal.setStatus(withdrawalStatus);
        withdrawalRepository.save(withdrawal);
    }
}
