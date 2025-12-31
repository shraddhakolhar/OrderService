package com.scaler.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarkOrderPaidRequest {

    @NotBlank
    private String paymentId;
}
