package com.bookstore.orderservice.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long id) {
        super("customer not found id: " + id);
    }
}