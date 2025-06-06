package edu.unimag.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class RequestPathLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RequestPathLoggingFilter.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        String method = request.getMethod().name();
        String path = uri.getPath();
        String query = uri.getQuery();
        String host = request.getHeaders().getFirst("Host"); // Opcional, pero útil

        // Registrar la información de la solicitud antes de que pase al siguiente filtro
        logger.info("INCOMING REQUEST: {} - Method: {}, Path: {}{}, Host: {}",
                LocalDateTime.now().format(FORMATTER),
                method,
                path,
                (query != null ? "?" + query : ""),
                host != null ? host : "N/A");

        // Continuar con la cadena de filtros
        return chain.filter(exchange)
                .doFinally(signalType -> {

                });
    }

    @Override
    public int getOrder() {

        return Ordered.HIGHEST_PRECEDENCE;
    }
}
