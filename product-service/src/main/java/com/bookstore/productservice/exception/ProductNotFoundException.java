package com.bookstore.productservice.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("product not found id: " + id);
    }
}