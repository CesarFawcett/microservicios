package edu.unimag.product.service;



import edu.unimag.product.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product createProduct(Product product);
    List<Product> getAllProducts();
    Optional<Product> getProductById(String id); // Usa String en lugar de UUID
    Product updateProduct(String id, Product productDetails);
    void deleteProduct(String id);
}