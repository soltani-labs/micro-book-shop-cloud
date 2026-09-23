package com.bookstore.orderservice.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long productId) {
        super("Insufficient stock for product id: " + productId);
    }
}