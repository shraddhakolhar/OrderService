package com.scaler.orderservice.controller;

import com.scaler.orderservice.dto.CheckoutRequest;
import com.scaler.orderservice.dto.OrderResponseDto;
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

    // CREATE ORDER (CHECKOUT)
    @PostMapping
    public ResponseEntity<OrderResponseDto> checkout(
            @Valid @RequestBody CheckoutRequest request,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(
                orderService.checkout(
                        authentication.getName(),
                        request,
                        authHeader
                )
        );
    }

    // PAYMENT WEBHOOK (Mock / Stripe)
    @PostMapping("/{orderId}/paid")
    public ResponseEntity<Void> markOrderPaid(
            @PathVariable Long orderId,
            @RequestBody String paymentId
    ) {
        orderService.markOrderPaid(orderId, paymentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(
                orderService.getOrderById(orderId)
        );
    }
}
