package edu.unimag.inventory.controller;

import edu.unimag.inventory.model.Inventory;
import edu.unimag.inventory.model.InventoryStatus;
import edu.unimag.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryControllerUnitTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

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

        when(inventoryService.createInventory(any(Inventory.class))).thenReturn(inventory);

        // Act
        ResponseEntity<Inventory> response = inventoryController.createInventory(inventory);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(inventory.getProductId(), response.getBody().getProductId());
        assertEquals(inventory.getQuantity(), response.getBody().getQuantity());
        assertEquals(inventory.getStatus(), response.getBody().getStatus());
        verify(inventoryService, times(1)).createInventory(any(Inventory.class));
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

        when(inventoryService.getInventoryById(id)).thenReturn(Optional.of(inventory));

        // Act
        ResponseEntity<Inventory> response = inventoryController.getInventoryById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(inventory.getId(), response.getBody().getId());
        assertEquals(inventory.getProductId(), response.getBody().getProductId());
        assertEquals(inventory.getQuantity(), response.getBody().getQuantity());
        assertEquals(inventory.getStatus(), response.getBody().getStatus());
        verify(inventoryService, times(1)).getInventoryById(id);
    }

    @Test
    void testGetInventoryByIdNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(inventoryService.getInventoryById(id)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Inventory> response = inventoryController.getInventoryById(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(inventoryService, times(1)).getInventoryById(id);
    }

    @Test
    void testGetAllInventory() {
        // Arrange
        Inventory inventory = Inventory.builder()
                .productId(UUID.randomUUID())
                .quantity(10)
                .status(InventoryStatus.IN_STOCK)
                .build();

        when(inventoryService.getAllInventory()).thenReturn(Collections.singletonList(inventory));

        // Act
        ResponseEntity<List<Inventory>> response = inventoryController.getAllInventory();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(inventory.getProductId(), response.getBody().get(0).getProductId());
        assertEquals(inventory.getQuantity(), response.getBody().get(0).getQuantity());
        assertEquals(inventory.getStatus(), response.getBody().get(0).getStatus());
        verify(inventoryService, times(1)).getAllInventory();
    }

    @Test
    void testUpdateInventory() {
        // Arrange
        UUID id = UUID.randomUUID();
        Inventory inventory = Inventory.builder()
                .id(id)
                .productId(UUID.randomUUID())
                .quantity(20)
                .status(InventoryStatus.LOW_STOCK)
                .build();

        when(inventoryService.updateInventory(id, inventory)).thenReturn(inventory);

        // Act
        ResponseEntity<Inventory> response = inventoryController.updateInventory(id, inventory);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(inventory.getId(), response.getBody().getId());
        assertEquals(inventory.getProductId(), response.getBody().getProductId());
        assertEquals(inventory.getQuantity(), response.getBody().getQuantity());
        assertEquals(inventory.getStatus(), response.getBody().getStatus());
        verify(inventoryService, times(1)).updateInventory(id, inventory);
    }

    @Test
    void testUpdateInventoryNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        Inventory inventory = Inventory.builder()
                .id(id)
                .productId(UUID.randomUUID())
                .quantity(20)
                .status(InventoryStatus.LOW_STOCK)
                .build();

        when(inventoryService.updateInventory(id, inventory)).thenThrow(new RuntimeException("Inventory not found"));

        // Act
        ResponseEntity<Inventory> response = inventoryController.updateInventory(id, inventory);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(inventoryService, times(1)).updateInventory(id, inventory);
    }
}