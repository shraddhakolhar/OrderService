package com.scaler.orderservice.controller;

import com.scaler.orderservice.dto.CheckoutRequest;
import com.scaler.orderservice.entity.OrderEntity;
import com.scaler.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderEntity> checkout(
            @Valid @RequestBody CheckoutRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                orderService.checkout(
                        authentication.getName(),
                        request,
                        authentication
                )
        );
    }
}
