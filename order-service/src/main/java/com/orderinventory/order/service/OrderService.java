package com.orderinventory.order.service;

import com.orderinventory.order.dto.OrderRequest;
import com.orderinventory.order.dto.OrderResponse;
import com.orderinventory.order.entity.Order;
import com.orderinventory.order.entity.OrderItem;
import com.orderinventory.order.entity.OrderStatus;
import com.orderinventory.order.event.OrderEventPublisher;
import com.orderinventory.order.event.OrderPlacedEvent;
import com.orderinventory.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        log.debug("Placing order for customer: {}", request.getCustomerId());

        List<OrderItem> items = request.getItems().stream()
                .map(item -> OrderItem.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .toList();

        BigDecimal totalAmount = items.stream()
                .map(item -> item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .items(items)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .build();

        Order saved = orderRepository.save(order);
        log.info("Order placed successfully: {}", saved.getId());

        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .eventId(UUID.randomUUID())
                .orderId(saved.getId())
                .customerId(saved.getCustomerId())
                .items(saved.getItems())
                .totalAmount(saved.getTotalAmount())
                .timestamp(saved.getCreatedAt())
                .build();

        orderEventPublisher.publishOrderPlaced(event);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId) {
        log.debug("Fetching order: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomer(UUID customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        log.debug("Updating order {} status to {}", orderId, status);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(status);
        orderRepository.save(order);
        log.info("Order {} status updated to {}", orderId, status);
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .items(order.getItems())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}