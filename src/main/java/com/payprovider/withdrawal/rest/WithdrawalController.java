package com.payprovider.withdrawal.rest;

import com.payprovider.withdrawal.model.WithdrawalStatus;
import com.payprovider.withdrawal.repository.PaymentMethodRepository;
import com.payprovider.withdrawal.repository.WithdrawalRepository;
import com.payprovider.withdrawal.repository.WithdrawalScheduledRepository;
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
import java.util.ArrayList;
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

    private final WithdrawalService withdrawalService;

    private final UserService userService;

    @PostMapping("/create-withdrawals")
    public ResponseEntity<String> create(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        String paymentMethodId = request.getParameter("paymentMethodId");
        String amount = request.getParameter("amount");
        String executeAt = request.getParameter("executeAt");
        if (userId == null || paymentMethodId == null || amount == null || executeAt == null) {
            return new ResponseEntity("Required params are missing", HttpStatus.BAD_REQUEST);
        }
        try {
            userController.findById(Long.parseLong(userId));
        } catch (Exception e) {
            return new ResponseEntity("User not found", HttpStatus.NOT_FOUND);
        }
        if (!context.getBean(PaymentMethodRepository.class).findById(Long.parseLong(paymentMethodId)).isPresent()) {
            return new ResponseEntity("Payment method not found", HttpStatus.NOT_FOUND);
        }

        WithdrawalService withdrawalService = context.getBean(WithdrawalService.class);
        Object body;
        if (executeAt.equals("ASAP")) {
            Withdrawal withdrawal = new Withdrawal();
            withdrawal.setUserId(Long.parseLong(userId));
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

        return new ResponseEntity(body, HttpStatus.OK);
    }

    @GetMapping("/find-all-withdrawals")
    public ResponseEntity findAll() {
        List<Withdrawal> withdrawals = context.getBean(WithdrawalRepository.class).findAll();
        List<WithdrawalScheduled> withdrawalsScheduled = context.getBean(WithdrawalScheduledRepository.class).findAll();
        List<Object> result = new ArrayList<>();
        result.addAll(withdrawals);
        result.addAll(withdrawalsScheduled);

        return new ResponseEntity(result, HttpStatus.OK);
    }
}
