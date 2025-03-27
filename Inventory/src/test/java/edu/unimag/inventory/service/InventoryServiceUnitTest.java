package edu.unimag.inventory.service;

import edu.unimag.inventory.model.Inventory;
import edu.unimag.inventory.model.InventoryStatus;
import edu.unimag.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceUnitTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateInventory() {
        // Arrange
        Inventory inventory = Inventory.builder()
                .productId(UUID.randomUUID())
                .quantity(10)
                .status(InventoryStatus.IN_STOCK)
                .build();

        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        // Act
        Inventory savedInventory = inventoryService.createInventory(inventory);

        // Assert
        assertNotNull(savedInventory);
        assertEquals(inventory.getProductId(), savedInventory.getProductId());
        assertEquals(inventory.getQuantity(), savedInventory.getQuantity());
        assertEquals(inventory.getStatus(), savedInventory.getStatus());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void testGetInventoryById() {
        // Arrange
        UUID id = UUID.randomUUID();
        Inventory inventory = Inventory.builder()
                .id(id)
                .productId(UUID.randomUUID())
                .quantity(10)
                .status(InventoryStatus.IN_STOCK)
                .build();

        when(inventoryRepository.findById(id)).thenReturn(Optional.of(inventory));

        // Act
        Optional<Inventory> foundInventory = inventoryService.getInventoryById(id);

        // Assert
        assertTrue(foundInventory.isPresent());
        assertEquals(inventory.getId(), foundInventory.get().getId());
        assertEquals(inventory.getProductId(), foundInventory.get().getProductId());
        assertEquals(inventory.getQuantity(), foundInventory.get().getQuantity());
        assertEquals(inventory.getStatus(), foundInventory.get().getStatus());
        verify(inventoryRepository, times(1)).findById(id);
    }

    @Test
    void testUpdateInventory() {
        // Arrange
        UUID id = UUID.randomUUID();
        Inventory existingInventory = Inventory.builder()
                .id(id)
                .productId(UUID.randomUUID())
                .quantity(10)
                .status(InventoryStatus.IN_STOCK)
                .build();

        Inventory updatedInventory = Inventory.builder()
                .id(id)
                .productId(UUID.randomUUID())
                .quantity(20)
                .status(InventoryStatus.LOW_STOCK)
                .build();

        when(inventoryRepository.findById(id)).thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(updatedInventory);

        // Act
        Inventory result = inventoryService.updateInventory(id, updatedInventory);

        // Assert
        assertNotNull(result);
        assertEquals(updatedInventory.getProductId(), result.getProductId());
        assertEquals(updatedInventory.getQuantity(), result.getQuantity());
        assertEquals(updatedInventory.getStatus(), result.getStatus());
        verify(inventoryRepository, times(1)).findById(id);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }
}