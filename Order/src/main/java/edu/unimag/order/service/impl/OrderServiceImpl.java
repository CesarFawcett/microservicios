package edu.unimag.order.service.impl;

import edu.unimag.order.model.Order;
import edu.unimag.order.repository.OrderRepository;
import edu.unimag.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry; 
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient; 
import reactor.core.publisher.Mono; 
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;
    private final WebClient inventoryWebClient; 

    public OrderServiceImpl(OrderRepository orderRepository, WebClient.Builder webClientBuilder) {
        this.orderRepository = orderRepository;
        this.webClient = webClientBuilder.baseUrl("http://product-service").build();
        this.inventoryWebClient = webClientBuilder.baseUrl("http://inventory-service").build(); 

    }

    @CircuitBreaker(name = "inventoryServiceBreaker", fallbackMethod = "updateInventoryFallback")
    @Retry(name = "inventoryServiceRetry")
    private Mono<Boolean> updateInventory(UUID productId, Integer quantity) {
    System.out.println("DEBUG: Llamando a inventory-service para actualizar producto ID: " + productId + ", Cantidad: " + quantity);

    return inventoryWebClient.patch() 
            .uri("/api/inventory/quantity/{productId}?quantity={quantity}", productId, quantity)
            .retrieve()
            .toBodilessEntity() 
            .map(response -> {
                boolean success = response.getStatusCode().is2xxSuccessful();
                if (success) {
                    System.out.println("INFO: Inventario actualizado con éxito para Producto ID: " + productId + ", Cantidad: " + quantity);
                } else {
                    System.err.println("ERROR: Fallo al actualizar inventario para Producto ID " + productId + ". Status: " + response.getStatusCode());
                }
                return success;
            })
            .doOnError(e -> System.err.println("ERROR: Fallo técnico al llamar inventory-service para actualizar ID " + productId + ": " + e.getMessage()));
    }

    private Mono<Boolean> updateInventoryFallback(UUID productId, Integer quantity, Throwable t) {
    System.err.println("FALLBACK: Circuit Breaker activado para inventoryServiceBreaker. No se pudo actualizar inventario para Producto ID: " + productId + ", Cantidad: " + quantity + ", Causa: " + t.getMessage());
    // decidir qué hacer en caso de fallo:
    return Mono.just(false); 
    }

    @Override
    public Order createOrder(Order order) {
    order.setCreatedAt(String.valueOf(LocalDateTime.now()));
    if (order.getItems() != null && !order.getItems().isEmpty()) {
        order.getItems().forEach(item -> {

            Mono<String> productDetailsMono = getProductDetails(item.getProductId());
            String productDetails = productDetailsMono.block(); 
            Mono<Boolean> inventoryUpdateMono = updateInventory(item.getProductId(), -item.getQuantity()); 
            boolean inventoryUpdated = inventoryUpdateMono.block(); 

            if (!inventoryUpdated) {
                System.err.println("ERROR: No se pudo actualizar el inventario para el producto " + item.getProductId() + ". Cancelando orden.");
                throw new RuntimeException("Insufficient stock or inventory service unavailable for product: " + item.getProductId());
            }
        });
    }

    Order savedOrder = orderRepository.save(order);
    return savedOrder;
    }

     private Mono<String> getProductDetails(UUID productId) {
        throw new UnsupportedOperationException("Unimplemented method 'getProductDetails'");
    }


    @CircuitBreaker(name = "productServiceBreaker", fallbackMethod = "getProductFallback")
    @Retry(name = "productServiceRetry")
    private Mono<String> getProductNameFromProductService(String productId) {
        System.out.println("DEBUG: Llamando a product-service por producto ID: " + productId);
        return webClient.get()
                .uri("/api/product/{id}", productId) 
                .retrieve()
                .bodyToMono(String.class) 
                .doOnError(e -> System.err.println("ERROR: Fallo al llamar product-service para ID " + productId + ": " + e.getMessage()));
    }

     private Mono<String> getProductFallback(String productId, Throwable t) {
        System.err.println("FALLBACK: Circuit Breaker activado para productServiceBreaker. Producto ID: " + productId + ", Causa: " + t.getMessage());
        return Mono.just("Producto-Nombre-No-Disponible-ID:" + productId); 
    }


    @Override
    public Optional<Order> getOrderById(UUID id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order updateOrder(UUID id, Order order) {
        if (orderRepository.existsById(id)) {
            order.setId(id);
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found with id: " + id);
    }

    @Override
    public void deleteOrder(UUID id) {
        orderRepository.deleteById(id);
    }
}