package edu.unimag.inventory.controller;

import edu.unimag.inventory.model.Inventory;
import edu.unimag.inventory.model.InventoryStatus;
import edu.unimag.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class InventoryControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

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
                .quantity(20)
                .status(InventoryStatus.IN_STOCK)
                .build();
        inventory = inventoryRepository.save(inventory);
    }

    @Test
    public void testCreateInventory() {
        Inventory newInventory = Inventory.builder()
                .productId(UUID.randomUUID())
                .quantity(15)
                .status(InventoryStatus.IN_STOCK)
                .build();

        ResponseEntity<Inventory> response = restTemplate.postForEntity("/api/inventory", newInventory, Inventory.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    public void testGetInventoryById() {
        ResponseEntity<Inventory> response = restTemplate.getForEntity("/api/inventory/" + inventory.getId(), Inventory.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(inventory.getId());
    }

    @Test
    public void testGetInventoryById_NotFound() {
        ResponseEntity<Inventory> response = restTemplate.getForEntity("/api/inventory/" + UUID.randomUUID(), Inventory.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testUpdateInventory() {
        inventory.setQuantity(50);
        HttpEntity<Inventory> requestUpdate = new HttpEntity<>(inventory);

        ResponseEntity<Inventory> response = restTemplate.exchange(
                "/api/inventory/" + inventory.getId(), HttpMethod.PUT, requestUpdate, Inventory.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getQuantity()).isEqualTo(50);
    }

//    @Test
//    public void testDeleteInventory() {
//        restTemplate.delete("/api/inventory/" + inventory.getId());
//        Optional<Inventory> deletedInventory = inventoryRepository.findById(inventory.getId());
//
//        assertThat(deletedInventory).isEmpty();
//    }

}
