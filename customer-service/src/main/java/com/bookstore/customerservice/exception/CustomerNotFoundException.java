package com.bookstore.customerservice.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long id) {
        super("customer not foundid: " + id);
    }
}