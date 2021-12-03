package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.dto.UserDto;
import com.payprovider.withdrawal.exception.EntityNotExistException;
import com.payprovider.withdrawal.mapper.UserMapper;
import com.payprovider.withdrawal.model.User;
import com.payprovider.withdrawal.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.payprovider.withdrawal.service.UserService.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;
    private UserMapper userMapper;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        userService = new UserService(userRepository, userMapper);
    }

    @Test
    public void shouldFindAllUsers() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("some name");

        UserDto userDto = UserDto.builder()
            .id(1L)
            .firstName("some name")
            .build();

        given(userRepository.findAll()).willReturn(List.of(user));
        given(userMapper.userToUserDtoList(List.of(user))).willReturn(List.of(userDto));

        List<UserDto> result = userService.findAll();

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
    }

    @Test
    public void shouldFindUserById() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("some name");

        UserDto userDto = UserDto.builder()
            .id(1L)
            .firstName("some name")
            .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userMapper.userToUserDto(user)).willReturn(userDto);

        UserDto result = userService.findByIdOrThrow(1L);

        assertEquals(userDto, result);
    }

    @Test
    public void shouldThrowEntityNotExistException() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        EntityNotExistException thrown = Assertions.assertThrows(EntityNotExistException.class, () -> {
            userService.findByIdOrThrow(1L);
        }, "EntityNotExistException was expected");

        assertEquals(USER_NOT_FOUND, thrown.getMessage());
    }
}
