package com.payprovider.withdrawal.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payprovider.withdrawal.dto.UserDto;
import com.payprovider.withdrawal.dto.WithdrawalDto;
import com.payprovider.withdrawal.model.PaymentMethod;
import com.payprovider.withdrawal.model.WithdrawalStatus;
import com.payprovider.withdrawal.service.PaymentMethodService;
import com.payprovider.withdrawal.service.UserService;
import com.payprovider.withdrawal.service.WithdrawalService;
import com.payprovider.withdrawal.model.Withdrawal;
import com.payprovider.withdrawal.model.WithdrawalScheduled;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ObjectMapper objectMapper;

    @Autowired
    private final WithdrawalService withdrawalService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final PaymentMethodService paymentMethodService;

    private static final String ASAP_WITHDRAWAL_VALUE = "ASAP";

    @PostMapping("/create-withdrawals")
    public ResponseEntity<String> create(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        String paymentMethodId = request.getParameter("paymentMethodId");
        String amount = request.getParameter("amount");
        String executeAt = request.getParameter("executeAt");

        if (userId == null || paymentMethodId == null || amount == null || executeAt == null) {
            return new ResponseEntity<>("Required params are missing", HttpStatus.BAD_REQUEST);
        }

        UserDto userDto;
        PaymentMethod paymentMethod;
        try {
            userDto = userService.findById(Long.parseLong(userId));
            paymentMethod = paymentMethodService.findById(Long.parseLong(paymentMethodId));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        String response;
        if (executeAt.equalsIgnoreCase(ASAP_WITHDRAWAL_VALUE)) {
            // TODO: might create a request DTO here and put creation logic to a service
            Withdrawal withdrawal = new Withdrawal();
            withdrawal.setUserId(userDto.getId());
            withdrawal.setPaymentMethodId(paymentMethod.getId());
            withdrawal.setAmount(Double.parseDouble(amount));
            withdrawal.setCreatedAt(Instant.now());
            withdrawal.setStatus(WithdrawalStatus.PENDING);

            withdrawalService.create(withdrawal);
            try {
                response = objectMapper.writeValueAsString(withdrawal);
            } catch (JsonProcessingException e) {
                log.error("Error processing withdrawal response {}", e.getMessage());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            // TODO: might create a request DTO here and put creation logic to a service
            WithdrawalScheduled withdrawalScheduled = new WithdrawalScheduled();
            withdrawalScheduled.setUserId(userDto.getId());
            withdrawalScheduled.setPaymentMethodId(paymentMethod.getId());
            withdrawalScheduled.setAmount(Double.parseDouble(amount));
            withdrawalScheduled.setCreatedAt(Instant.now());
            withdrawalScheduled.setExecuteAt(Instant.parse(executeAt));
            withdrawalScheduled.setStatus(WithdrawalStatus.PENDING);

            withdrawalService.schedule(withdrawalScheduled);
            try {
                response = objectMapper.writeValueAsString(withdrawalScheduled);
            } catch (JsonProcessingException e) {
                log.error("Error processing withdrawal scheduled response {}", e.getMessage());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
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
