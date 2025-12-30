package com.scaler.orderservice.repository;

import com.scaler.orderservice.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByUserEmail(String userEmail);
}
