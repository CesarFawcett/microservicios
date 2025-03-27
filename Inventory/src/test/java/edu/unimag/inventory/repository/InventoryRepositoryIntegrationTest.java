package edu.unimag.inventory.repository;

import edu.unimag.inventory.model.Inventory;
import edu.unimag.inventory.model.InventoryStatus;
import edu.unimag.inventory.service.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InventoryRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    void saveInventory() {
        // Arrange
        Inventory inventory = Inventory.builder()
                .productId(UUID.randomUUID())
                .quantity(10)
                .status(InventoryStatus.IN_STOCK)
                .build();

        // Act
        Inventory savedInventory = inventoryRepository.save(inventory);

        // Assert
        assertNotNull(savedInventory.getId());
        assertEquals(inventory.getProductId(), savedInventory.getProductId());
    }

    @Test
    void findById() {
        // Arrange
        Inventory inventory = Inventory.builder()
                .productId(UUID.randomUUID())
                .quantity(10)
                .status(InventoryStatus.IN_STOCK)
                .build();
        Inventory savedInventory = inventoryRepository.save(inventory);

        // Act
        Optional<Inventory> foundInventory = inventoryRepository.findById(savedInventory.getId());

        // Assert
        assertTrue(foundInventory.isPresent());
        assertEquals(savedInventory.getId(), foundInventory.get().getId());
    }

    @Test
    void findByProductId() {
        // Arrange
        UUID productId = UUID.randomUUID();
        Inventory inventory = Inventory.builder()
                .productId(productId)
                .quantity(10)
                .status(InventoryStatus.IN_STOCK)
                .build();
        inventoryRepository.save(inventory);

        // Act
        Optional<Inventory> foundInventory = inventoryRepository.findByProductId(productId);

        // Assert
        assertTrue(foundInventory.isPresent());
        assertEquals(productId, foundInventory.get().getProductId());
    }

    @Test
    void findByStatus() {
        // Arrange
        Inventory inventory1 = Inventory.builder()
                .productId(UUID.randomUUID())
                .quantity(10)
                .status(InventoryStatus.IN_STOCK)
                .build();
        Inventory inventory2 = Inventory.builder()
                .productId(UUID.randomUUID())
                .quantity(5)
                .status(InventoryStatus.IN_STOCK)
                .build();
        inventoryRepository.saveAll(List.of(inventory1, inventory2));

        // Act
        List<Inventory> foundInventories = inventoryRepository.findByStatus(InventoryStatus.IN_STOCK);

        // Assert
        assertEquals(2, foundInventories.size());
    }
}