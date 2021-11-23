package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.exception.TransactionException;
import com.payprovider.withdrawal.model.User;

import java.util.List;

public interface UserService {

    User findById(Long id) throws TransactionException;

    List<User> findAll();
}
