package com.payprovider.withdrawal.rest;

import com.payprovider.withdrawal.dto.UserDto;
import com.payprovider.withdrawal.exception.TransactionException;
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

    @GetMapping("/find-all-users")
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/find-user-by-id/{id}")
    public UserDto findById(@PathVariable Long id) throws TransactionException {
        return userService.findById(id);
    }
}
