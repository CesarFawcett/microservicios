// product/src/main/java/edu/unimag/product/controller/CacheController.java

package edu.unimag.product.controller;

// Elimina esta importación de Caffeine, ya no es necesaria
// import com.github.benmanes.caffeine.cache.stats.CacheStats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cache") 
public class CacheController {

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/stats")
    public String getCacheStats() {
        Cache cache = cacheManager.getCache("products"); 

        if (cache == null) {
            return "Caché 'products' no encontrado. Verifica el nombre en tu CacheConfig.";
        }

        return "Caché 'products' está configurado y usa Redis. Las estadísticas detalladas deben consultarse directamente en el servidor Redis.";
    }

    // Opcional: Un endpoint para limpiar la caché manualmente (útil para pruebas)
    @GetMapping("/clear")
    public String clearCache() {
        Cache cache = cacheManager.getCache("products");
        if (cache != null) {
            cache.clear(); // Limpia la caché "products"
            return "Caché 'products' ha sido limpiado con éxito.";
        }
        return "Caché 'products' no encontrado para limpiar.";
    }
}