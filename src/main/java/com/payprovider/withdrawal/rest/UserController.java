package com.payprovider.withdrawal.rest;

import com.payprovider.withdrawal.model.User;
import com.payprovider.withdrawal.repository.UserRepository;
import com.payprovider.withdrawal.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Autowired
    private ApplicationContext context;

    @GetMapping("/find-all-users")
    public List<User> findAll() {

        return context.getBean(UserRepository.class).findAll();
    }

    @GetMapping("/find-user-by-id/{id}")
    public User findById(@PathVariable Long id) {

        return context.getBean(UserRepository.class).findById(id).orElseThrow();
    }
}
