package edu.unimag.apigateway;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @GetMapping("/fallback/inventory")
    public Mono<String> inventoryFallback() {
        return Mono.just("El servicio de inventario no está disponible en este momento. Intente más tarde.");
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @GetMapping("/fallback/order")
    public Mono<String> orderFallback() {
        return Mono.just("El servicio de order no está disponible en este momento. Intente más tarde.");
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @GetMapping("/fallback/payment")
    public Mono<String> paymentFallback() {
        return Mono.just("El servicio de payment no está disponible en este momento. Intente más tarde.");
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @GetMapping("/fallback/product")
    public Mono<String> productFallback() {
        return Mono.just("El servicio de product no está disponible en este momento. Intente más tarde.");
    }
    // Agrega otros métodos de fallback para los demás servicios
}
