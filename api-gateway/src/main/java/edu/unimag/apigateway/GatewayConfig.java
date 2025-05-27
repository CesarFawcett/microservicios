package edu.unimag.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, CorrelationIdFilter correlationIdFilter) {
        return builder.routes()
                // Ruta para inventory-service
                .route("inventory-service", r -> r.path("/api/inventory/**")
                        .filters(f -> f.filter(correlationIdFilter.apply(new CorrelationIdFilter.Config()))
                                .circuitBreaker(config -> config
                                        .setName("inventoryCircuitBreaker") // Nombre del Circuit Breaker
                                        .setFallbackUri("forward:/fallback/inventory") // Fallback URI
                                )
                                .retry(retryConfig -> retryConfig.setRetries(2)) // 2 reintentos
                        )
                        .uri("lb://inventory-service"))

                // Ruta para order-service
                .route("order-service", r -> r.path("/api/order/**")
                        .filters(f -> f.filter(correlationIdFilter.apply(new CorrelationIdFilter.Config()))
                                .circuitBreaker(config -> config
                                        .setName("orderCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/order"))
                                .retry(retryConfig -> retryConfig.setRetries(2))
                        )
                        .uri("lb://order-service"))

                // Ruta para payment-service
                .route("payment-service", r -> r.path("/api/payment/**")
                        .filters(f -> f.filter(correlationIdFilter.apply(new CorrelationIdFilter.Config()))
                                .circuitBreaker(config -> config
                                        .setName("paymentCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/payment"))
                                .retry(retryConfig -> retryConfig.setRetries(2))
                        )
                        .uri("lb://payment-service"))

                // Ruta para product-service
                .route("product-service", r -> r.path("/api/product/**")
                        .filters(f -> f.filter(correlationIdFilter.apply(new CorrelationIdFilter.Config()))
                                .circuitBreaker(config -> config
                                        .setName("productCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/product"))
                                .retry(retryConfig -> retryConfig.setRetries(2))
                        )
                        .uri("lb://product-service"))
                .build();
    }
}