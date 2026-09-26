package com.orderinventory.order.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private static final String ORDER_PLACED_TOPIC = "order.placed";

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void publishOrderPlaced(OrderPlacedEvent event) {
        log.info("Publishing OrderPlacedEvent for orderId: {}", event.getOrderId());

        kafkaTemplate.send(ORDER_PLACED_TOPIC, event.getOrderId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish OrderPlacedEvent for orderId: {}",
                                event.getOrderId(), ex);
                    } else {
                        log.info("OrderPlacedEvent published successfully for orderId: {} | " +
                                        "partition: {} | offset: {}",
                                event.getOrderId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}