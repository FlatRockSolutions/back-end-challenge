package com.payprovider.withdrawal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.payprovider.withdrawal.dto.PaymentProcessingRequest;
import com.payprovider.withdrawal.exception.TransactionException;
import com.payprovider.withdrawal.model.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
public class WithdrawalProcessingService {

    @Value("provider.url")
    private String paymentProviderUrl;

    @Autowired
    private RestTemplate restTemplate;

    public Long sendToProcessing(Double amount, PaymentMethod paymentMethod) throws TransactionException {
        PaymentProcessingRequest paymentProcessingRequest = PaymentProcessingRequest.builder()
                .amount(amount)
                .paymentMethod(paymentMethod).build();
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String processPaymentPayload = objectWriter.writeValueAsString(paymentProcessingRequest);
            HttpHeaders headers = new HttpHeaders();
            headers.add(ACCEPT, APPLICATION_JSON_VALUE);
            headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);

            HttpEntity<String> requestEntity = new HttpEntity<>(processPaymentPayload, headers);

            ResponseEntity<String> out = restTemplate.postForEntity(paymentProviderUrl, requestEntity, String.class);
            HttpStatus httpStatus = out.getStatusCode();

            if (httpStatus.isError()) {
                throw new TransactionException("Error while processing payment transaction");
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing payment payload {} ", e.getMessage());
        }
        return System.nanoTime();
    }
}
