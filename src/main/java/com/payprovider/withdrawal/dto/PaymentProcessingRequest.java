package com.payprovider.withdrawal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.payprovider.withdrawal.model.PaymentMethod;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentProcessingRequest {

    @JsonProperty("amount")
    private double amount;

    @JsonProperty("paymentMethod")
    private PaymentMethod paymentMethod;

}
