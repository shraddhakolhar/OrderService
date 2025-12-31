package com.scaler.orderservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemResponseDto {

    private Long productId;
    private Double price;
    private Integer quantity;
    private Double itemTotal;
}
