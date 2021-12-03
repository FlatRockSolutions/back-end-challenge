package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.dto.UserDto;
import com.payprovider.withdrawal.exception.EntityNotExistException;
import com.payprovider.withdrawal.mapper.UserMapper;
import com.payprovider.withdrawal.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    public static final String USER_NOT_FOUND = "User not found";
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> findAll() {
        return userMapper.userToUserDtoList(userRepository.findAll());
    }

    /**
     * Return UserDto if exist with userId ot throw EntityNotExistException.
     *
     * @param userId
     * @return UserDto if exist with userId
     */
    public UserDto findByIdOrThrow(Long userId) {
        return userMapper.userToUserDto(userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotExistException(USER_NOT_FOUND)));
    }
}
