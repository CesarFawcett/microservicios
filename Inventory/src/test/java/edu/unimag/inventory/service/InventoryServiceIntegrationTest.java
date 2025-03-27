package edu.unimag.inventory.service;

import edu.unimag.inventory.model.Inventory;
import edu.unimag.inventory.model.InventoryStatus;
import edu.unimag.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class InventoryServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private Inventory inventory;

    @BeforeEach
    public void setUp() {
        inventoryRepository.deleteAll();
        inventory = Inventory.builder()
                .productId(UUID.randomUUID())
                .quantity(30)
                .status(InventoryStatus.IN_STOCK)
                .build();
        inventory = inventoryService.createInventory(inventory);
    }

    @Test
    public void testCreateInventory() {
        Inventory newInventory = Inventory.builder()
                .productId(UUID.randomUUID())
                .quantity(15)
                .status(InventoryStatus.IN_STOCK)
                .build();

        Inventory savedInventory = inventoryService.createInventory(newInventory);

        assertThat(savedInventory).isNotNull();
        assertThat(savedInventory.getId()).isNotNull();
    }

    @Test
    public void testGetInventoryById() {
        Optional<Inventory> foundInventory = inventoryService.getInventoryById(inventory.getId());

        assertThat(foundInventory).isPresent();
        assertThat(foundInventory.get().getId()).isEqualTo(inventory.getId());
    }

    @Test
    public void testUpdateInventory() {
        inventory.setQuantity(50);
        Inventory updatedInventory = inventoryService.updateInventory(inventory.getId(), inventory);

        assertThat(updatedInventory.getQuantity()).isEqualTo(50);
    }

    @Test
    public void testDeleteInventory() {
        inventoryService.deleteInventory(inventory.getId());
        Optional<Inventory> deletedInventory = inventoryRepository.findById(inventory.getId());

        assertThat(deletedInventory).isEmpty();
    }

    @Test
    public void testGetInventoryByStatus() {
        List<Inventory> inventories = inventoryService.getInventoryByStatus(InventoryStatus.IN_STOCK);

        assertThat(inventories).isNotEmpty();
        assertThat(inventories).contains(inventory);
    }
}
