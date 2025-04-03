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
                .route("inventory-service", r -> r.path("/api/inventory/**")
                        .filters(f -> f.filter(correlationIdFilter.apply(new CorrelationIdFilter.Config())))
                        .uri("lb://inventory-service"))
                .route("order-service", r -> r.path("/api/order/**")
                        .filters(f -> f.filter(correlationIdFilter.apply(new CorrelationIdFilter.Config())))
                        .uri("lb://order-service"))
                .route("payment-service", r -> r.path("/api/payment/**")
                        .filters(f -> f.filter(correlationIdFilter.apply(new CorrelationIdFilter.Config())))
                        .uri("lb://payment-service"))
                .route("product-service", r -> r.path("/api/product/**")
                        .filters(f -> f.filter(correlationIdFilter.apply(new CorrelationIdFilter.Config())))
                        .uri("lb://product-service"))
                .build();
    }
}