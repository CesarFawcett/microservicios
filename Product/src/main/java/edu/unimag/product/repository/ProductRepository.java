package edu.unimag.product.repository;

import edu.unimag.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    // Métodos personalizados si los necesitas
}