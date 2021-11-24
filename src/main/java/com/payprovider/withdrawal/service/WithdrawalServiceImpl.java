package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.exception.TransactionException;
import com.payprovider.withdrawal.model.PaymentMethod;
import com.payprovider.withdrawal.model.Withdrawal;
import com.payprovider.withdrawal.model.WithdrawalScheduled;
import com.payprovider.withdrawal.model.WithdrawalStatus;
import com.payprovider.withdrawal.repository.PaymentMethodRepository;
import com.payprovider.withdrawal.repository.WithdrawalRepository;
import com.payprovider.withdrawal.repository.WithdrawalScheduledRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class WithdrawalServiceImpl implements WithdrawalService {

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @Autowired
    private WithdrawalScheduledRepository withdrawalScheduledRepository;

    @Autowired
    private WithdrawalProcessingService withdrawalProcessingService;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private EventsService eventsService;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void create(Withdrawal withdrawal) {
        Withdrawal pendingWithdrawal = withdrawalRepository.save(withdrawal);

        executorService.submit(() -> {
            Optional<Withdrawal> savedWithdrawalOptional = withdrawalRepository.findById(pendingWithdrawal.getId());

            PaymentMethod paymentMethod = savedWithdrawalOptional.flatMap(value -> paymentMethodRepository.findById(value.getPaymentMethodId())).orElse(null);

            if (savedWithdrawalOptional.isPresent() && paymentMethod != null) {
                Withdrawal savedWithdrawal = savedWithdrawalOptional.get();
                try {
                    var transactionId = withdrawalProcessingService.sendToProcessing(withdrawal.getAmount(), paymentMethod);
                    savedWithdrawal.setStatus(WithdrawalStatus.PROCESSING);
                    savedWithdrawal.setTransactionId(transactionId);
                    withdrawalRepository.save(savedWithdrawal);
                    eventsService.send(savedWithdrawal);
                } catch (Exception e) {
                    if (e instanceof TransactionException) {
                        savedWithdrawal.setStatus(WithdrawalStatus.FAILED);
                    } else {
                        savedWithdrawal.setStatus(WithdrawalStatus.INTERNAL_ERROR);
                    }
                    withdrawalRepository.save(savedWithdrawal);
                    eventsService.send(savedWithdrawal);
                }
            }
        });
    }

    @Override
    public void schedule(WithdrawalScheduled withdrawalScheduled) {
        withdrawalScheduledRepository.save(withdrawalScheduled);
    }

    @Override
    @Scheduled(fixedDelay = 5000)
    public void run() {
        withdrawalScheduledRepository.findAllByExecuteAtBefore(Instant.now()).forEach(this::processScheduled);
    }

    @Override
    public List<Withdrawal> findAllWithdrawals() {
        return withdrawalRepository.findAll();
    }

    @Override
    public List<WithdrawalScheduled> findAllScheduledWithdrawals() {
        return withdrawalScheduledRepository.findAll();
    }

    private void processScheduled(WithdrawalScheduled withdrawal) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(withdrawal.getPaymentMethodId()).orElse(null);
        if (paymentMethod != null) {
            try {
                var transactionId = withdrawalProcessingService.sendToProcessing(withdrawal.getAmount(), paymentMethod);
                withdrawal.setStatus(WithdrawalStatus.PROCESSING);
                withdrawal.setTransactionId(transactionId);
                withdrawalScheduledRepository.save(withdrawal);
                eventsService.send(withdrawal);
            } catch (Exception e) {
                if (e instanceof TransactionException) {
                    withdrawal.setStatus(WithdrawalStatus.FAILED);
                } else {
                    withdrawal.setStatus(WithdrawalStatus.INTERNAL_ERROR);
                }
                withdrawalScheduledRepository.save(withdrawal);
                eventsService.send(withdrawal);
            }
        }
    }
}
