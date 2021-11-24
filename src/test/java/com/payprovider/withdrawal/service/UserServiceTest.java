package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.Application;
import com.payprovider.withdrawal.dto.UserDto;
import com.payprovider.withdrawal.exception.TransactionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class
)
@ContextConfiguration
public class UserServiceTest {

    private static final Long TEST_USER_ID = 1L;
    private static final Long INCORRECT_TEST_USER_ID = 4L;
    private static final int DEFAULT_USERS_SIZE = 3;

    @Autowired
    private UserService userService;

    @Test
    public void testFindById() throws TransactionException {
        UserDto userDto = userService.findById(TEST_USER_ID);
        assertNotNull(userDto);
        assertThat(userDto.getId().equals(1L));
        assertThat(userDto.getFirstName().equals("David"));
        assertThat(userDto.getMaxWithdrawalAmount().equals(100.0));
    }

    @Test
    public void testFindAll() {
        List<UserDto> userDtoList = userService.findAll();
        assertNotNull(userDtoList);
        assertThat(userDtoList.size() == DEFAULT_USERS_SIZE);
    }

    @Test(expected = TransactionException.class)
    public void testUserNotFound() throws TransactionException {
        userService.findById(INCORRECT_TEST_USER_ID);
    }
}
