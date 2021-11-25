package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.dto.UserDto;
import com.payprovider.withdrawal.exception.TransactionException;
import com.payprovider.withdrawal.exception.UserNotFoundException;
import com.payprovider.withdrawal.model.User;
import com.payprovider.withdrawal.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDto findById(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("No user found", id));
        return UserDto
                .builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .paymentMethods(user.getPaymentMethods())
                .maxWithdrawalAmount(user.getMaxWithdrawalAmount()).build();
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(this::toUserDto).collect(Collectors.toList());
    }

    private UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setPaymentMethods(user.getPaymentMethods());
        userDto.setMaxWithdrawalAmount(user.getMaxWithdrawalAmount());
        return userDto;
    }
}
