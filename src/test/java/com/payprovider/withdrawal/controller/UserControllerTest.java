package com.payprovider.withdrawal.controller;

import com.payprovider.withdrawal.Application;
import com.payprovider.withdrawal.dto.UserDto;
import com.payprovider.withdrawal.repository.UserRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class
)
@ContextConfiguration
public class UserControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userRepository.deleteAll();
        testRestTemplate = new TestRestTemplate();
    }

    @Test
    public void testFindAll() {
        ResponseEntity<UserDto[]> response = testRestTemplate.getForEntity("/find-all-users", UserDto[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void testFindUser() {
        ResponseEntity<UserDto> response = testRestTemplate.getForEntity("/find-user-by-id/1", UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void testUserNotFound() {
        testRestTemplate.getForEntity("/find-user-by-id/5", UserDto.class);
    }
}
