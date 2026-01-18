package com.scaler.orderservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OrderItemResponseDto {

    private Long productId;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal itemTotal;
}
