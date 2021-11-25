package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.dto.UserDto;
import com.payprovider.withdrawal.exception.TransactionException;

import java.util.List;

public interface UserService {

    UserDto findById(Long id) throws TransactionException;

    List<UserDto> findAll();
}
