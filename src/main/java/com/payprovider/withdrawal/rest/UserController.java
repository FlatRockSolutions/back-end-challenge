package com.payprovider.withdrawal.rest;

import com.payprovider.withdrawal.dto.UserDto;
import com.payprovider.withdrawal.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "User", description = "The user API")
public class UserController {

    public static final String FIND_ALL_USERS = "/find-all-users";
    public static final String FIND_USER_BY_ID_ID = "/find-user-by-id/{id}";
    private final UserService userService;

    @Operation(summary = "Find all users.", tags = {"user"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation.")})
    @GetMapping(FIND_ALL_USERS)
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @Operation(summary = "Find user by id.", tags = {"user"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation."),
        @ApiResponse(responseCode = "404", description = "User not found.")})
    @GetMapping(FIND_USER_BY_ID_ID)
    public UserDto findById(@PathVariable Long id) {
        return userService.findByIdOrThrow(id);
    }
}
