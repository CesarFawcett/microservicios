package edu.unimag.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends AbstractGatewayFilterFactory<CorrelationIdFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private static final String CORRELATION_ID_HEADER = "correlationId";

    public CorrelationIdFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Generar correlationId si no existe en la solicitud entrante
            String correlationId = exchange.getRequest()
                    .getHeaders()
                    .getFirst(CORRELATION_ID_HEADER);

            if (correlationId == null || correlationId.isEmpty()) {
                correlationId = UUID.randomUUID().toString();
                logger.info("Generated new correlationId: {}", correlationId);
            }

            // Agregar el correlationId a la solicitud
            ServerHttpRequest request = exchange.getRequest()
                    .mutate()
                    .header(CORRELATION_ID_HEADER, correlationId)
                    .build();

            // Agregar el correlationId a los headers de respuesta
            exchange.getResponse()
                    .getHeaders()
                    .add(CORRELATION_ID_HEADER, correlationId);

            logger.info("Added correlationId to request and response: {}", correlationId);

            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    public static class Config {
        // Puedes agregar propiedades de configuración aquí si necesitas
    }
}
