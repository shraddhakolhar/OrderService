package com.scaler.orderservice.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CartResponse {

    private List<CartItemResponse> items;
    private Double cartTotal;
}
