package com.payprovider.withdrawal.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payprovider.withdrawal.dto.RequestWithdrawalDto;
import com.payprovider.withdrawal.dto.WithdrawalDto;
import com.payprovider.withdrawal.exception.EntityNotExistException;
import com.payprovider.withdrawal.model.WithdrawalStatus;
import com.payprovider.withdrawal.model.WithdrawalType;
import com.payprovider.withdrawal.service.WithdrawalService;
import com.payprovider.withdrawal.validator.WithdrawalValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static com.payprovider.withdrawal.rest.WithdrawalController.CREATE_WITHDRAWALS;
import static com.payprovider.withdrawal.rest.WithdrawalController.FIND_ALL_WITHDRAWALS;
import static com.payprovider.withdrawal.service.UserService.USER_NOT_FOUND;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(WithdrawalController.class)
class WithdrawalControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private WithdrawalService withdrawalService;

    @MockBean
    private WithdrawalValidator withdrawalValidator;

    @Test
    public void shouldValidateAndCreateWithdrawal() throws Exception {
        RequestWithdrawalDto withdrawalDto = RequestWithdrawalDto.builder()
            .withdrawalType(WithdrawalType.ASAP)
            .amount(new BigDecimal("100.0"))
            .paymentMethodId(2L)
            .userId(1L)
            .build();
        WithdrawalDto withdrawalDtoAfterUpdate = WithdrawalDto.builder()
            .withdrawalType(WithdrawalType.ASAP)
            .amount(new BigDecimal("100.0"))
            .paymentMethodId(2L)
            .userId(1L)
            .id(3L)
            .executeAt(Instant.now())
            .status(WithdrawalStatus.PROCESSING)
            .build();
        given(withdrawalService.create(withdrawalDto)).willReturn(withdrawalDtoAfterUpdate);

        mvc.perform(post(CREATE_WITHDRAWALS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(withdrawalDto)))
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.withdrawalType", is(WithdrawalType.ASAP.toString())))
            .andExpect(jsonPath("$.amount", is(100.0)))
            .andExpect(jsonPath("$.paymentMethodId", is(2)))
            .andExpect(jsonPath("$.userId", is(1)))
            .andExpect(jsonPath("$.id", is(3)))
            .andExpect(jsonPath("$.executeAt", is(withdrawalDtoAfterUpdate.getExecuteAt().toString())))
            .andExpect(jsonPath("$.status", is(WithdrawalStatus.PROCESSING.toString())));

        verify(withdrawalValidator, times(1)).validate(withdrawalDto);
        verify(withdrawalService, times(1)).create(withdrawalDto);
    }

    @Test
    public void shouldNotPassValidationAndNotCreateWithdrawalIfUserNotExist() throws Exception {
        RequestWithdrawalDto withdrawalDto = RequestWithdrawalDto.builder()
            .withdrawalType(WithdrawalType.ASAP)
            .amount(new BigDecimal("100.0"))
            .paymentMethodId(2L)
            .userId(1L)
            .build();

        doThrow(new EntityNotExistException(USER_NOT_FOUND)).when(withdrawalValidator).validate(withdrawalDto);

        mvc.perform(post(CREATE_WITHDRAWALS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(withdrawalDto)))
            .andExpect(status().isNotFound())
            .andExpect(content().string(USER_NOT_FOUND));

        verify(withdrawalValidator, times(1)).validate(withdrawalDto);
        verify(withdrawalService, times(0)).create(any());
    }

    @Test
    public void shouldNotPassValidationAndNotCreateWithdrawalIfUseNotValidData() throws Exception {
        WithdrawalDto withdrawalDto = WithdrawalDto.builder()
            .amount(new BigDecimal("-100.0"))
            .build();
        mvc.perform(post(CREATE_WITHDRAWALS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(withdrawalDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.withdrawalType", is("Withdrawal type name is mandatory")))
            .andExpect(jsonPath("$.amount", is("Amount must be greater than 0.0")))
            .andExpect(jsonPath("$.paymentMethodId", is("Payment method id is mandatory")))
            .andExpect(jsonPath("$.userId", is("User id is mandatory")));

        verify(withdrawalValidator, times(0)).validate(any());
        verify(withdrawalService, times(0)).create(any());
    }

    @Test
    public void shouldFindAllWithdrawals() throws Exception {
        WithdrawalDto withdrawalDto = WithdrawalDto.builder()
            .withdrawalType(WithdrawalType.ASAP)
            .amount(new BigDecimal("100.0"))
            .paymentMethodId(2L)
            .userId(1L)
            .id(3L)
            .executeAt(Instant.now())
            .status(WithdrawalStatus.PROCESSING)
            .build();

        given(withdrawalService.findAll()).willReturn(List.of(withdrawalDto));

        mvc.perform(get(FIND_ALL_WITHDRAWALS)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].withdrawalType", is(WithdrawalType.ASAP.toString())))
            .andExpect(jsonPath("$[0].amount", is(100.0)))
            .andExpect(jsonPath("$[0].paymentMethodId", is(2)))
            .andExpect(jsonPath("$[0].userId", is(1)))
            .andExpect(jsonPath("$[0].id", is(3)))
            .andExpect(jsonPath("$[0].executeAt", is(withdrawalDto.getExecuteAt().toString())))
            .andExpect(jsonPath("$[0].status", is(WithdrawalStatus.PROCESSING.toString())));
    }
}
