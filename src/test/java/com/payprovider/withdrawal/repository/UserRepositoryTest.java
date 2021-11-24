package com.payprovider.withdrawal.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payprovider.withdrawal.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserRepositoryTest {

    private static final long USER1_ID = 1L;
    private static final String USER_1_FIRST_NAME = "Test1";

    private static final long USER2_ID = 2L;
    private static final String USER_2_FIRST_NAME = "Test2";

    private static final long USER3_ID = 3L;
    private static final String USER_3_FIRST_NAME = "Test3";

    private static final long USER4_ID = 4L;
    private static final String USER_4_FIRST_NAME = "Test4";

    @SpyBean
    private UserRepository userRepository;

    @AfterEach
    public void clearRepository() {
        userRepository.deleteAll();
    }

    @Test
    public void save() {
        User initialUser = new User();
        initialUser.setId(USER1_ID);
        initialUser.setFirstName(USER_1_FIRST_NAME);
        User actualUser = userRepository.save(initialUser);
        assertThat(actualUser, is(samePropertyValuesAs(initialUser)));
    }

    @Test
    public void invalidJsonOnSave() {
        assertThrows(Exception.class, () -> {
            User initialUser = new User();
            initialUser.setId(USER1_ID);
            initialUser.setFirstName(USER_1_FIRST_NAME);
            ObjectMapper mockObjectMapper = Mockito.mock(ObjectMapper.class);
            Mockito.when(mockObjectMapper.writeValueAsString(Mockito.notNull())).thenThrow(JsonProcessingException.class);
            userRepository.save(initialUser);
        });
    }

    @Test
    public void saveAll() {
        User user1 = new User();
        user1.setId(USER1_ID);
        user1.setFirstName(USER_1_FIRST_NAME);

        User user2 = new User();
        user1.setId(USER2_ID);
        user1.setFirstName(USER_2_FIRST_NAME);

        List<User> initialusers = new ArrayList<>(asList(user1, user2));
        List<User> actualusers = userRepository.saveAll(initialusers);
        assertThat(actualusers, is(samePropertyValuesAs(initialusers)));
    }

    @Test
    public void findById() {
        User user1 = new User();
        user1.setId(USER1_ID);
        user1.setFirstName(USER_1_FIRST_NAME);
        userRepository.save(user1);
        Optional<User> actualUser = userRepository.findById(USER1_ID);
        assertTrue(actualUser.isPresent());
        assertThat(actualUser.get(), is(samePropertyValuesAs(user1)));
    }

    @Test
    public void findAll() {
        User user3 = new User();
        user3.setId(USER3_ID);
        user3.setFirstName(USER_3_FIRST_NAME);

        User user4 = new User();
        user4.setId(USER4_ID);
        user4.setFirstName(USER_4_FIRST_NAME);

        List<User> expectedusers = new ArrayList<>(asList(user3, user4));
        userRepository.saveAll(expectedusers);

        List<User> actualUsers = userRepository.findAll();
        assertThat(actualUsers, is(samePropertyValuesAs(expectedusers)));
    }

}
