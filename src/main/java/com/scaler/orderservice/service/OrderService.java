package com.scaler.orderservice.service;

import com.scaler.orderservice.dto.CartItemResponse;
import com.scaler.orderservice.dto.CartResponse;
import com.scaler.orderservice.dto.CheckoutRequest;
import com.scaler.orderservice.entity.*;
import com.scaler.orderservice.repository.OrderItemRepository;
import com.scaler.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestTemplate restTemplate;

    @Value("${cart.service.url}")
    private String cartServiceUrl;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            RestTemplate restTemplate
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public OrderEntity checkout(String userEmail, CheckoutRequest request, Authentication authentication) {

        // 🔵 1. Forward JWT to CartService
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authentication.getCredentials().toString());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<CartResponse> cartResponse =
                restTemplate.exchange(
                        cartServiceUrl + "/cart",
                        HttpMethod.GET,
                        entity,
                        CartResponse.class
                );

        CartResponse cart = cartResponse.getBody();

        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 🔵 2. Create Order
        OrderEntity order = OrderEntity.builder()
                .userEmail(userEmail)
                .totalAmount(cart.getCartTotal())
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        OrderEntity savedOrder = orderRepository.save(order);

        // 🔵 3. Create OrderItems (snapshot)
        List<OrderItemEntity> orderItems =
                cart.getItems().stream().map(item ->
                        OrderItemEntity.builder()
                                .order(savedOrder)
                                .productId(item.getProductId())
                                .price(item.getPrice())
                                .quantity(item.getQuantity())
                                .itemTotal(item.getItemTotal())
                                .build()
                ).toList();

        orderItemRepository.saveAll(orderItems);
        savedOrder.setItems(orderItems);

        // 🔵 4. Clear cart
        restTemplate.exchange(
                cartServiceUrl + "/cart/delete",
                HttpMethod.DELETE,
                entity,
                Void.class
        );

        return savedOrder;
    }
}
