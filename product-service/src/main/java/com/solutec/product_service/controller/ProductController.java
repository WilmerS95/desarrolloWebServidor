package com.solutec.product_service.controller;

import com.solutec.product_service.model.Product;
import com.solutec.product_service.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/public/hello")
    public String publicHello() {
        return "Hola, este endpoint es público";
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return service.getAllProducts();
    }

    @PostMapping("/products")
    public Product createProduct(@RequestBody Product product) {
        return service.saveProduct(product);
    }
}
