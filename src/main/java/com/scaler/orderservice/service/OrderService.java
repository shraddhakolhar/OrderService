package com.scaler.orderservice.service;

import com.scaler.orderservice.dto.*;
import com.scaler.orderservice.entity.*;
import com.scaler.orderservice.repository.OrderRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    public OrderService(
            OrderRepository orderRepository,
            RestTemplate restTemplate
    ) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public OrderResponseDto checkout(
            String userEmail,
            CheckoutRequest request,
            String bearerToken
    ) {

        // =========================
        // FETCH CART FROM CART-SERVICE (EUREKA)
        // =========================
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", bearerToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<CartResponse> cartResponse =
                restTemplate.exchange(
                        "http://CART-SERVICE/cart",
                        HttpMethod.GET,
                        entity,
                        CartResponse.class
                );

        CartResponse cart = cartResponse.getBody();

        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // =========================
        // CREATE ORDER
        // =========================
        OrderEntity order = OrderEntity.builder()
                .userEmail(userEmail)
                .totalAmount(cart.getCartTotal())
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        List<OrderItemEntity> orderItems =
                cart.getItems().stream().map(item ->
                        OrderItemEntity.builder()
                                .order(order)
                                .productId(item.getProductId())
                                .price(item.getPrice())
                                .quantity(item.getQuantity())
                                .itemTotal(item.getItemTotal())
                                .build()
                ).toList();

        order.getItems().addAll(orderItems);

        OrderEntity savedOrder = orderRepository.save(order);

        // =========================
        // CLEAR CART
        // =========================
        restTemplate.exchange(
                "http://CART-SERVICE/cart/delete",
                HttpMethod.DELETE,
                entity,
                Void.class
        );

        return mapToResponseDto(savedOrder);
    }

    // ==========================
    // DTO Mapper
    // ==========================
    private OrderResponseDto mapToResponseDto(OrderEntity order) {

        List<OrderItemResponseDto> itemDtos =
                order.getItems().stream().map(item ->
                        OrderItemResponseDto.builder()
                                .productId(item.getProductId())
                                .price(item.getPrice())
                                .quantity(item.getQuantity())
                                .itemTotal(item.getItemTotal())
                                .build()
                ).toList();

        return OrderResponseDto.builder()
                .id(order.getId())
                .userEmail(order.getUserEmail())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentId(order.getPaymentId())
                .paidAt(order.getPaidAt())
                .createdAt(order.getCreatedAt())
                .items(itemDtos)
                .build();
    }

    // ==========================
    // PAYMENT CALLBACK
    // ==========================
    @Transactional
    public void markOrderPaid(Long orderId, String paymentId) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.PAID) {
            return;
        }

        order.setStatus(OrderStatus.PAID);
        order.setPaymentId(paymentId);
        order.setPaidAt(LocalDateTime.now());

        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return mapToResponseDto(order);
    }
}
