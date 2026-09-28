package com.orderinventory.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {

    private UUID productId;
    private String productName;
    private Integer quantityAvailable;
    private Integer quantityReserved;
    private OffsetDateTime updatedAt;
}