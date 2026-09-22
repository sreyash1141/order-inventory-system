package com.orderinventory.order.repository;

import com.orderinventory.order.entity.Order;
import com.orderinventory.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByCustomerId(UUID customerId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByCustomerIdAndStatus(UUID customerId, OrderStatus status);
}