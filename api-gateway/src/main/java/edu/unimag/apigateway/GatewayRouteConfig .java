import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Ruta para inventory-service
                .route("inventory-service", r -> r.path("/api/inventory/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://inventory-service"))

                // Ruta para order-service
                .route("order-service", r -> r.path("/api/order/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://order-service"))

                // Ruta para payment-service
                .route("payment-service", r -> r.path("/api/payment/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://payment-service"))

                // Ruta para product-service
                .route("product-service", r -> r.path("/api/product/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://product-service"))

                .build();
    }
}