package com.orderinventory.inventory.service;

import com.orderinventory.inventory.dto.InventoryRequest;
import com.orderinventory.inventory.dto.InventoryResponse;
import com.orderinventory.inventory.entity.Inventory;
import com.orderinventory.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public InventoryResponse createInventory(InventoryRequest request) {
        if (inventoryRepository.existsByProductId(request.getProductId())) {
            throw new IllegalArgumentException(
                    "Inventory already exists for product: " + request.getProductId());
        }

        Inventory inventory = Inventory.builder()
                .productId(request.getProductId())
                .productName(request.getProductName())
                .quantityAvailable(request.getQuantityAvailable())
                .quantityReserved(0)
                .build();

        Inventory saved = inventoryRepository.save(inventory);
        log.info("Created inventory for product {} with {} units",
                saved.getProductId(), saved.getQuantityAvailable());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getByProductId(UUID productId) {
        return inventoryRepository.findByProductId(productId)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getAll() {
        return inventoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .productId(inventory.getProductId())
                .productName(inventory.getProductName())
                .quantityAvailable(inventory.getQuantityAvailable())
                .quantityReserved(inventory.getQuantityReserved())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}