package com.payprovider.withdrawal.controller;

import com.payprovider.withdrawal.Application;
import com.payprovider.withdrawal.model.Withdrawal;
import com.payprovider.withdrawal.model.WithdrawalScheduled;
import com.payprovider.withdrawal.model.WithdrawalStatus;
import com.payprovider.withdrawal.repository.UserRepository;
import com.payprovider.withdrawal.repository.WithdrawalRepository;
import com.payprovider.withdrawal.repository.WithdrawalScheduledRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class
)
public class WithdrawalControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @Autowired
    private WithdrawalScheduledRepository withdrawalScheduledRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userRepository.deleteAll();
        withdrawalRepository.deleteAll();
        testRestTemplate = new TestRestTemplate();
    }

    @Test
    public void testFindAll() {
        initWithdrawals();
        ResponseEntity<Object> response = testRestTemplate.getForEntity("/find-all-withdrawals", Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void testCreateWithdrawalAsap() {
        ResponseEntity<String> response = testRestTemplate
                .postForEntity("/create-withdrawals?userId=1&paymentMethodId=1&amount=100.0&executeAt=ASAP", null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        Optional<Withdrawal> withdrawal = withdrawalRepository.findById(1L);
        assertThat(withdrawal.isPresent());
        assertThat(withdrawal.get().getUserId().equals(1L));
        assertThat(withdrawal.get().getPaymentMethodId().equals(1L));
        assertThat(withdrawal.get().getAmount().equals(100.0));
    }

    @Test
    public void testCreateWithdrawalScheduled() {
        ResponseEntity<String> response = testRestTemplate
                .postForEntity("/create-withdrawals?userId=1&paymentMethodId=1&amount=100.0&executeAt=2021-04-09T10:15:30.00Z", null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        Optional<WithdrawalScheduled> withdrawalScheduled = withdrawalScheduledRepository.findById(1L);
        assertThat(withdrawalScheduled.isPresent());
        assertThat(withdrawalScheduled.get().getUserId().equals(1L));
        assertThat(withdrawalScheduled.get().getPaymentMethodId().equals(1L));
        assertThat(withdrawalScheduled.get().getAmount().equals(100.0));
    }

    @Test
    public void testCreateWithdrawalWithErrorResponse() {
        ResponseEntity<String> response = testRestTemplate
                .postForEntity("/create-withdrawals?userId=1&paymentMethodId=1&amount=100.0", null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
    }

    private void initWithdrawals() {
        withdrawalScheduledRepository.deleteAll();

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setUserId(1L);
        withdrawal.setPaymentMethodId(1L);
        withdrawal.setAmount(100.0);
        withdrawal.setCreatedAt(Instant.now());
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        withdrawalRepository.save(withdrawal);

        WithdrawalScheduled withdrawalScheduled = new WithdrawalScheduled();
        withdrawalScheduled.setUserId(1L);
        withdrawalScheduled.setPaymentMethodId(1L);
        withdrawalScheduled.setAmount(100.0);
        withdrawalScheduled.setCreatedAt(Instant.now());
        withdrawalScheduled.setExecuteAt(Instant.parse("2021-04-09T10:15:30.00Z"));
        withdrawalScheduled.setStatus(WithdrawalStatus.PENDING);
        withdrawalScheduledRepository.save(withdrawalScheduled);
    }

}
