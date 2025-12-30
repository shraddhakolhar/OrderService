package com.scaler.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequest {

    @NotBlank
    private String address;

    @NotBlank
    private String paymentMethod;
}
