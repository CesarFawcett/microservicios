package edu.unimag.inventory.service;

import edu.unimag.inventory.model.Inventory;
import edu.unimag.inventory.model.InventoryStatus;
import edu.unimag.inventory.repository.InventoryRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry; 
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient; 

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WebClient webClient; 

    public InventoryServiceImpl(InventoryRepository inventoryRepository, WebClient.Builder webClientBuilder) {
        this.inventoryRepository = inventoryRepository;
        this.webClient = webClientBuilder.baseUrl("http://localhost:9999/mock-audit").build(); 
    }

    @Override
    public Inventory createInventory(Inventory inventory) {
        Inventory savedInventory = inventoryRepository.save(inventory);
        // Simular una llamada a un servicio externo después de crear el inventario
        sendAuditLog(savedInventory.getId().toString(), "created", savedInventory.getQuantity());
        return savedInventory;
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
            Inventory updatedInventory = inventoryRepository.save(existingInventory);

            sendAuditLog(updatedInventory.getId().toString(), "updated", updatedInventory.getQuantity());
            return updatedInventory;
        }
        throw new RuntimeException("Inventory not found with id: " + id);
    }

    @Override
    public void deleteInventory(UUID id) {
        inventoryRepository.deleteById(id);
        sendAuditLog(id.toString(), "deleted", 0); 
    }

    @Override
    public boolean updateQuantity(UUID productId, Integer quantity) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(productId);
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            int oldQuantity = inventory.getQuantity();
            inventory.setQuantity(quantity);

            // Actualizar el estado según la cantidad
            if (quantity <= 0) {
                inventory.setStatus(InventoryStatus.OUT_OF_STOCK);
            } else {
                inventory.setStatus(InventoryStatus.IN_STOCK);
            }

            Inventory updatedInventory = inventoryRepository.save(inventory);

            sendAuditLog(updatedInventory.getId().toString(), "quantity_updated", updatedInventory.getQuantity());
            return true;
        }
        return false;
    }

    @Override
    public List<Inventory> getInventoryByStatus(InventoryStatus status) {
        return inventoryRepository.findByStatus(status);
    }

    // --- Métodos para la simulación del Circuit Breaker ---

    @CircuitBreaker(name = "auditServiceBreaker", fallbackMethod = "auditFallback")
    @Retry(name = "auditServiceRetry")
    private void sendAuditLog(String inventoryId, String action, Integer quantity) {
        System.out.println("DEBUG: Enviando log de auditoría para Inventory ID: " + inventoryId + ", Acción: " + action + ", Cantidad: " + quantity);
        
        webClient.post()
                .uri("/") 
                .bodyValue("{\"inventoryId\": \"" + inventoryId + "\", \"action\": \"" + action + "\", \"quantity\": " + quantity + "}")
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(response -> System.out.println("INFO: Log de auditoría enviado con éxito. Status: " + response.getStatusCode()))
                .doOnError(e -> System.err.println("ERROR: Fallo al enviar log de auditoría para Inventory ID " + inventoryId + ": " + e.getMessage()))
                .block();
    }


    private void auditFallback(String inventoryId, String action, Integer quantity, Throwable t) {
        System.err.println("FALLBACK: Circuit Breaker activado para auditServiceBreaker. No se pudo enviar log de auditoría para Inventory ID: " + inventoryId + ", Acción: " + action + ", Causa: " + t.getMessage());
  
    }
}