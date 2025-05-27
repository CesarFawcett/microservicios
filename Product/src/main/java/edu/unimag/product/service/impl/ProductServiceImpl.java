package edu.unimag.product.service.impl;

import edu.unimag.product.model.Product;
import edu.unimag.product.repository.ProductRepository;
import edu.unimag.product.service.ProductService;
import edu.unimag.product.service.config.CacheConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    @CacheEvict(value = CacheConfig.PRODUCT_CACHE, allEntries = true)
    public Product createProduct(Product product) {
        product.setCreatedAt(new Date());
        product.setUpdatedAt(new Date());
        return productRepository.save(product);
    }

    @Override
    @Cacheable(value = CacheConfig.PRODUCT_CACHE)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Cacheable(value = CacheConfig.PRODUCT_CACHE, key = "#id")
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id); // Devuelve un Optional<Product>
    }

    @Override
    @CachePut(value = CacheConfig.PRODUCT_CACHE, key = "#id") 
    public Product updateProduct(String id, Product productDetails) {
        // Buscar el producto por ID
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found")); // Lanza una excepción si no se encuentra

        // Actualizar los campos del producto
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setUpdatedAt(new Date());

        // Guardar el producto actualizado
        return productRepository.save(product);
    }

    @Override
    @CacheEvict(value = CacheConfig.PRODUCT_CACHE, key = "#id")
    public void deleteProduct(String id) {
        productRepository.deleteById(id); // Llama al método deleteById del repositorio
    }
}