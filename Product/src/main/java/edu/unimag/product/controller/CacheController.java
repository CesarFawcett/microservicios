package edu.unimag.product.controller;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
public class CacheController {

    @Autowired
    private CacheManager cacheManager;  // Inyecta el CacheManager

    @GetMapping("/stats")
    public String getCacheStats() {
        // Obtiene el caché por su nombre (ej: "payments")
        Cache cache = cacheManager.getCache("products");

        if (cache == null) {
            return "Caché no encontrado. Verifica el nombre.";
        }

        // Convierte el caché nativo de Caffeine para acceder a las estadísticas
        com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache =
                (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();

        CacheStats stats = caffeineCache.stats();
        return String.format(
                "Estadísticas del Caché 'products':<br>" +
                        "• Hits (éxitos): %d<br>" +
                        "• Misses (fallos): %d<br>" +
                        "• Evictions (eliminaciones): %d<br>" +
                        "• Tiempo promedio de carga: %.2f ms",
                stats.hitCount(),
                stats.missCount(),
                stats.evictionCount(),
                stats.averageLoadPenalty() / 1_000_000.0  // Convierte nanosegundos a milisegundos
        );
    }
}
