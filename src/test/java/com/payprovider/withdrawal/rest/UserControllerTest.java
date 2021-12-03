package com.payprovider.withdrawal.rest;

import com.payprovider.withdrawal.dto.PaymentMethodDto;
import com.payprovider.withdrawal.dto.UserDto;
import com.payprovider.withdrawal.exception.EntityNotExistException;
import com.payprovider.withdrawal.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static com.payprovider.withdrawal.rest.UserController.FIND_ALL_USERS;
import static com.payprovider.withdrawal.rest.UserController.FIND_USER_BY_ID_ID;
import static com.payprovider.withdrawal.service.UserService.USER_NOT_FOUND;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    public void shouldReturnListUsers() throws Exception {
        PaymentMethodDto paymentMethodDto = PaymentMethodDto.builder()
            .id(2L)
            .name("testMethod")
            .build();
        UserDto userDto = UserDto.builder()
            .id(1L)
            .firstName("testName")
            .maxWithdrawalAmount(new BigDecimal("100.0"))
            .paymentMethods(List.of(paymentMethodDto))
            .build();

        given(userService.findAll()).willReturn(List.of(userDto));

        mvc.perform(get(FIND_ALL_USERS)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("testName")))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].maxWithdrawalAmount", is(100.0)))
            .andExpect(jsonPath("$[0].paymentMethods", hasSize(1)))
            .andExpect(jsonPath("$[0].paymentMethods[0].name", is("testMethod")))
            .andExpect(jsonPath("$[0].paymentMethods[0].id", is(2)));
    }

    @Test
    public void shouldReturnListUserById() throws Exception {
        PaymentMethodDto paymentMethodDto = PaymentMethodDto.builder()
            .id(2L)
            .name("testMethod")
            .build();
        UserDto userDto = UserDto.builder()
            .id(1L)
            .firstName("testName")
            .maxWithdrawalAmount(new BigDecimal("100.0"))
            .paymentMethods(List.of(paymentMethodDto))
            .build();

        given(userService.findByIdOrThrow(1L)).willReturn(userDto);

        mvc.perform(get(FIND_USER_BY_ID_ID, 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("testName")))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.maxWithdrawalAmount", is(100.0)))
            .andExpect(jsonPath("$.paymentMethods", hasSize(1)))
            .andExpect(jsonPath("$.paymentMethods[0].name", is("testMethod")))
            .andExpect(jsonPath("$.paymentMethods[0].id", is(2)));
    }

    @Test
    public void shouldHaveNotFoundStatusIfUserDoesNotExist() throws Exception {
        given(userService.findByIdOrThrow(1L)).willThrow(new EntityNotExistException(USER_NOT_FOUND));

        mvc.perform(get(FIND_USER_BY_ID_ID, 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string(USER_NOT_FOUND));
    }
}
