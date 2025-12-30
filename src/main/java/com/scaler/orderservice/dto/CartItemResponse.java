package com.scaler.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemResponse {

    private Long productId;
    private String productName;
    private double price;
    private int quantity;
    private double itemTotal;
}
