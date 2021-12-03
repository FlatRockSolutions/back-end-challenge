package com.payprovider.withdrawal.rest;

import com.payprovider.withdrawal.dto.RequestWithdrawalDto;
import com.payprovider.withdrawal.dto.WithdrawalDto;

import com.payprovider.withdrawal.service.WithdrawalService;
import com.payprovider.withdrawal.validator.WithdrawalValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;


@RestController
@AllArgsConstructor
@Tag(name = "Withdrawal", description = "The withdrawal API")
public class WithdrawalController {

    public static final String CREATE_WITHDRAWALS = "/create-withdrawals";
    public static final String FIND_ALL_WITHDRAWALS = "/find-all-withdrawals";
    private final WithdrawalService withdrawalService;
    private final WithdrawalValidator withdrawalValidator;

    @Operation(summary = "Creating withdrawal.", tags = {"withdrawal"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Accepting withdrawal creating operation."),
        @ApiResponse(responseCode = "400", description = "Lack/incorrect of amount, userId, paymentMethodId or withdrawalType."),
        @ApiResponse(responseCode = "404", description = "User or payment method not found."),
        @ApiResponse(responseCode = "422", description = "Validation exception")})
    @PostMapping(CREATE_WITHDRAWALS)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public WithdrawalDto create(@Valid @RequestBody RequestWithdrawalDto withdrawalDto) {
        withdrawalValidator.validate(withdrawalDto);
        return withdrawalService.create(withdrawalDto);
    }

    @Operation(summary = "Find all withdrawals.", tags = {"withdrawal"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation.")})
    @GetMapping(FIND_ALL_WITHDRAWALS)
    public List<WithdrawalDto> findAll() {
        return withdrawalService.findAll();
    }
}
