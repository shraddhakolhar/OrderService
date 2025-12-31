package com.scaler.orderservice.dto;

import com.scaler.orderservice.entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderResponseDto {

    private Long id;
    private String userEmail;
    private Double totalAmount;
    private OrderStatus status;

    private String paymentId;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    private List<OrderItemResponseDto> items;
}
