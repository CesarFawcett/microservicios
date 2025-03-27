package edu.unimag.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;


@Data // Lombok: Genera getters, setters, toString, equals, hashCode
@AllArgsConstructor // Lombok: Genera un constructor con todos los campos
@Builder
@Document(collection = "products")
public class Product {
    @Id
    private String id;

    private String name;
    private String description;
    private double price;
    private int stock;
    private Date createdAt;
    private Date updatedAt;

    // Constructor vacío (necesario para Spring Data MongoDB)
    public Product() {
        this.id = UUID.randomUUID().toString(); // Genera un UUID como String
    }

    // Constructor con parámetros
    public Product(String name, String description, double price, int stock) {
        this.id = UUID.randomUUID().toString(); // Genera un UUID como String
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
}