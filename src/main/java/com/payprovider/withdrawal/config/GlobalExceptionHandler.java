package com.payprovider.withdrawal.config;

import com.payprovider.withdrawal.exception.EntityNotExistException;
import com.payprovider.withdrawal.exception.ForbiddenOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for converting exceptions to proper response statuses, log the error
 * and provide error message to the body.
 */

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handler for EntityNotExistException. Return 404 code to response.
     *
     * @param ex EntityNotExistException
     * @return error message
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {EntityNotExistException.class})
    @ResponseBody
    public String entityNotExist(EntityNotExistException ex) {
        return processingException(ex);
    }

    /**
     * Handler for ForbiddenOperationException. Return 422 code to response.
     *
     * @param ex BadRequestException
     * @return error message
     */
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(value = {ForbiddenOperationException.class})
    @ResponseBody
    public String forbiddenOperation(ForbiddenOperationException ex) {
        return processingException(ex);
    }

    /**
     * Handler for BadRequestException. Return 400 code to response.
     *
     * @param ex BadRequestException
     * @return error message
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Map<String, String> handleValidationExceptions(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error(ex.getMessage() + errors, ex);
        return errors;
    }

    private String processingException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ex.getMessage();
    }
}
