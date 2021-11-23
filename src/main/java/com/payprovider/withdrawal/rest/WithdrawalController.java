package com.payprovider.withdrawal.rest;

import com.payprovider.withdrawal.dto.WithdrawalDto;
import com.payprovider.withdrawal.exception.TransactionException;
import com.payprovider.withdrawal.model.PaymentMethod;
import com.payprovider.withdrawal.model.User;
import com.payprovider.withdrawal.model.WithdrawalStatus;
import com.payprovider.withdrawal.repository.PaymentMethodRepository;
import com.payprovider.withdrawal.service.PaymentMethodService;
import com.payprovider.withdrawal.service.UserService;
import com.payprovider.withdrawal.service.WithdrawalService;
import com.payprovider.withdrawal.model.Withdrawal;
import com.payprovider.withdrawal.model.WithdrawalScheduled;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;

@Slf4j
@Api
@RestController
@RequiredArgsConstructor
public class WithdrawalController {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserController userController;

    @Autowired
    private final WithdrawalService withdrawalService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final PaymentMethodService paymentMethodService;

    @PostMapping("/create-withdrawals")
    public ResponseEntity<String> create(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        String paymentMethodId = request.getParameter("paymentMethodId");
        String amount = request.getParameter("amount");
        String executeAt = request.getParameter("executeAt");

        if (userId == null || paymentMethodId == null || amount == null || executeAt == null) {
            return new ResponseEntity<>("Required params are missing", HttpStatus.BAD_REQUEST);
        }

        User user;
        PaymentMethod paymentMethod;
        try {
            user = userService.findById(Long.parseLong(userId));
            paymentMethod = paymentMethodService.findById(Long.parseLong(paymentMethodId));

        } catch (Exception e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        if (!context.getBean(PaymentMethodRepository.class).findById(Long.parseLong(paymentMethodId)).isPresent()) {
            return new ResponseEntity<>("Payment method not found", HttpStatus.NOT_FOUND);
        }

        Object body;
        if (executeAt.equals("ASAP")) {
            Withdrawal withdrawal = new Withdrawal();
            withdrawal.setUserId(user.getId());
            withdrawal.setPaymentMethodId(Long.parseLong(paymentMethodId));
            withdrawal.setAmount(Double.parseDouble(amount));
            withdrawal.setCreatedAt(Instant.now());
            withdrawal.setStatus(WithdrawalStatus.PENDING);

            withdrawalService.create(withdrawal);
            body = withdrawal;
        } else {
            WithdrawalScheduled withdrawalScheduled = new WithdrawalScheduled();
            withdrawalScheduled.setUserId(Long.parseLong(userId));
            withdrawalScheduled.setPaymentMethodId(Long.parseLong(paymentMethodId));
            withdrawalScheduled.setAmount(Double.parseDouble(amount));
            withdrawalScheduled.setCreatedAt(Instant.now());
            withdrawalScheduled.setExecuteAt(Instant.parse(executeAt));
            withdrawalScheduled.setStatus(WithdrawalStatus.PENDING);

            withdrawalService.schedule(withdrawalScheduled);
            body = withdrawalScheduled;
        }

        return new ResponseEntity(body, HttpStatus.CREATED);
    }

    @GetMapping("/find-all-withdrawals")
    public ResponseEntity<WithdrawalDto> findAll() {
        List<Withdrawal> withdrawals = withdrawalService.findAllWithdrawals();
        List<WithdrawalScheduled> withdrawalsScheduled = withdrawalService.findAllScheduledWithdrawals();

        WithdrawalDto withdrawalDto = WithdrawalDto.builder()
                .withdrawals(withdrawals)
                .withdrawalScheduled(withdrawalsScheduled)
                .build();
        return new ResponseEntity<>(withdrawalDto, HttpStatus.OK);
    }
}
