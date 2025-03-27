package edu.unimag.inventory.service;

import edu.unimag.inventory.model.Inventory;
import edu.unimag.inventory.model.InventoryStatus;
import edu.unimag.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Inventory createInventory(Inventory inventory) {

        return inventoryRepository.save(inventory);
    }

    @Override
    public Optional<Inventory> getInventoryById(UUID id) {
        return inventoryRepository.findById(id);
    }

    @Override
    public Optional<Inventory> getInventoryByProductId(UUID productId) {
        return inventoryRepository.findByProductId(productId);
    }

    @Override
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    @Override
    public Inventory updateInventory(UUID id, Inventory inventory) {
        Optional<Inventory> existingInventoryOpt = inventoryRepository.findById(id);
        if (existingInventoryOpt.isPresent()) {
            Inventory existingInventory = existingInventoryOpt.get();
            existingInventory.setProductId(inventory.getProductId());
            existingInventory.setQuantity(inventory.getQuantity());
            existingInventory.setStatus(inventory.getStatus());
            return inventoryRepository.save(existingInventory);
        }
        throw new RuntimeException("Inventory not found with id: " + id);
    }

    @Override
    public void deleteInventory(UUID id) {
        inventoryRepository.deleteById(id);
    }

    @Override
    public boolean updateQuantity(UUID productId, Integer quantity) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(productId);
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            inventory.setQuantity(quantity);

            // Actualizar el estado según la cantidad
            if (quantity <= 0) {
                inventory.setStatus(InventoryStatus.OUT_OF_STOCK);
            } else {
                inventory.setStatus(InventoryStatus.IN_STOCK);
            }

            inventoryRepository.save(inventory);
            return true;
        }
        return false;
    }

    @Override
    public List<Inventory> getInventoryByStatus(InventoryStatus status) {
        return inventoryRepository.findByStatus(status);
    }
}
