package edu.unimag.inventory.repository;

import edu.unimag.inventory.model.Inventory;
import edu.unimag.inventory.model.InventoryStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryRepositoryUnitTest {

    @Mock
    private InventoryRepository inventoryRepository;
    private Inventory inventory;

    @BeforeEach
    public void setUp() {
        inventory = Inventory.builder()
                .productId(UUID.randomUUID())
                .quantity(25)
                .status(InventoryStatus.IN_STOCK)
                .build();
    }

    @Test
    public void testFindByProductId() {
        // Simula el comportamiento del repository
        when(inventoryRepository.findByProductId(inventory.getProductId()))
                .thenReturn(Optional.of(inventory));

        Optional<Inventory> foundInventory = inventoryRepository.findByProductId(inventory.getProductId());

        assertThat(foundInventory).isPresent();
        assertThat(foundInventory.get().getProductId()).isEqualTo(inventory.getProductId());

        verify(inventoryRepository, times(1)).findByProductId(inventory.getProductId());
    }

    @Test
    public void testFindByProductId_NotFound() {
        when(inventoryRepository.findByProductId(any(UUID.class)))
                .thenReturn(Optional.empty());

        Optional<Inventory> foundInventory = inventoryRepository.findByProductId(UUID.randomUUID());

        assertThat(foundInventory).isNotPresent();

        verify(inventoryRepository, times(1)).findByProductId(any(UUID.class));
    }

    @Test
    public void testFindByStatus() {
        when(inventoryRepository.findByStatus(InventoryStatus.IN_STOCK))
                .thenReturn(List.of(inventory));

        List<Inventory> foundInventories = inventoryRepository.findByStatus(InventoryStatus.IN_STOCK);

        assertThat(foundInventories).isNotEmpty();
        assertThat(foundInventories).contains(inventory);

        verify(inventoryRepository, times(1)).findByStatus(InventoryStatus.IN_STOCK);
    }
}

