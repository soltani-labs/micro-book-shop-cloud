package com.bookstore.productservice.service;

import com.bookstore.productservice.dto.ProductRequest;
import com.bookstore.productservice.dto.ProductResponse;
import com.bookstore.productservice.entity.Product;
import com.bookstore.productservice.exception.ProductNotFoundException;
import com.bookstore.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product(
                request.getTitle(),
                request.getAuthor(),
                request.getPrice(),
                request.getStockQuantity()
        );
        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return toResponse(product);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setTitle(request.getTitle());
        product.setAuthor(request.getAuthor());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        Product updated = productRepository.save(product);
        return toResponse(updated);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    // appelle vers order service
    public boolean isInStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        return product.getStockQuantity() >= quantity;
    }

    public void reduceStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getTitle(),
                product.getAuthor(),
                product.getPrice(),
                product.getStockQuantity()
        );
    }
}