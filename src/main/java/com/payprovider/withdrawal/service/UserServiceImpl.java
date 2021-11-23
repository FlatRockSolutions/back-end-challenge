package com.payprovider.withdrawal.service;

import com.payprovider.withdrawal.exception.TransactionException;
import com.payprovider.withdrawal.exception.UserNotFoundException;
import com.payprovider.withdrawal.model.User;
import com.payprovider.withdrawal.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findById(Long id) throws TransactionException {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("No user found", id));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
