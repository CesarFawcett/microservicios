package edu.unimag.order.service.impl;

import edu.unimag.order.model.Order;
import edu.unimag.order.model.OrderItem; // Asegúrate de importar OrderItem
import edu.unimag.order.repository.OrderRepository;
import edu.unimag.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker; // Importar
import io.github.resilience4j.retry.annotation.Retry; // Importar, si usas reintentos
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient; // Importar
import reactor.core.publisher.Mono; // Importar

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient; // Inyectar WebClient

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, WebClient.Builder webClientBuilder) {
        this.orderRepository = orderRepository;
        // Configura WebClient para comunicarse con el product-service
        this.webClient = webClientBuilder.baseUrl("http://product-service").build();
    }

    @Override
    public Order createOrder(Order order) {
        order.setCreatedAt(String.valueOf(LocalDateTime.now()));

         if (order.getItems() != null && !order.getItems().isEmpty()) {
            order.getItems().forEach(item -> {
                // Llamada al microservicio de productos con Circuit Breaker y Retry
                // Usamos .block() aquí para simplificar, pero en un contexto reactivo real,
                // deberías componer los Monos/Fluxes.
                String productName = getProductNameFromProductService(item.getProductId().toString()).block();
                System.out.println("DEBUG: Producto ID " + item.getProductId() + " -> Nombre: " + productName);
                // Aquí podrías enriquecer el OrderItem con el nombre del producto, etc.
                // item.setProductName(productName); // Asumiendo que OrderItem tiene un campo para nombre de producto
            });
        }

        return orderRepository.save(order);
    }

     @CircuitBreaker(name = "productServiceBreaker", fallbackMethod = "getProductFallback")
    @Retry(name = "productServiceRetry")
    private Mono<String> getProductNameFromProductService(String productId) {
        System.out.println("DEBUG: Llamando a product-service por producto ID: " + productId);
        return webClient.get()
                .uri("/api/product/{id}", productId) // Asume que product-service tiene un endpoint /api/product/{id}
                .retrieve()
                .bodyToMono(String.class) // Asume que el servicio de producto devuelve un String o un objeto simple
                .doOnError(e -> System.err.println("ERROR: Fallo al llamar product-service para ID " + productId + ": " + e.getMessage()));
    }

     private Mono<String> getProductFallback(String productId, Throwable t) {
        System.err.println("FALLBACK: Circuit Breaker activado para productServiceBreaker. Producto ID: " + productId + ", Causa: " + t.getMessage());
        return Mono.just("Producto-Nombre-No-Disponible-ID:" + productId); // Devolver un valor por defecto
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