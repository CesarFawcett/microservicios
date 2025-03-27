package edu.unimag.inventory.service;


import edu.unimag.inventory.model.Inventory;
import edu.unimag.inventory.model.InventoryStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryService {

    Inventory createInventory(Inventory inventory);

    Optional<Inventory> getInventoryById(UUID id);

    Optional<Inventory> getInventoryByProductId(UUID productId);

    List<Inventory> getAllInventory();

    Inventory updateInventory(UUID id, Inventory inventory);

    void deleteInventory(UUID id);

    boolean updateQuantity(UUID productId, Integer quantity);

    List<Inventory> getInventoryByStatus(InventoryStatus status);
}