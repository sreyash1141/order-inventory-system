package com.orderinventory.order.event;

import com.orderinventory.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedEvent {

    private UUID eventId;
    private UUID orderId;
    private UUID customerId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private OffsetDateTime timestamp;
}