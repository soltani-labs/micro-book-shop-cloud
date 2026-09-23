package com.bookstore.orderservice.service;

import com.bookstore.orderservice.client.CustomerClient;
import com.bookstore.orderservice.client.ProductClient;
import com.bookstore.orderservice.dto.CustomerResponse;
import com.bookstore.orderservice.dto.ProductResponse;
import com.bookstore.orderservice.exception.CustomerNotFoundException;
import com.bookstore.orderservice.exception.ServiceUnavailableException;
import feign.FeignException;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

@Service
public class ResilientClientService {

    private final ProductClient productClient;
    private final CustomerClient customerClient;
    private final CircuitBreaker productCircuitBreaker;
    private final CircuitBreaker customerCircuitBreaker;

    public ResilientClientService(ProductClient productClient,
                                  CustomerClient customerClient,
                                  CircuitBreakerFactory circuitBreakerFactory) {
        this.productClient = productClient;
        this.customerClient = customerClient;
        this.productCircuitBreaker = circuitBreakerFactory.create("productService");
        this.customerCircuitBreaker = circuitBreakerFactory.create("customerService");
    }

    public void validateCustomerExists(Long customerId) {
        customerCircuitBreaker.run(
                () -> {
                    try {
                        customerClient.getCustomerById(customerId);
                        return null;
                    } catch (FeignException.NotFound ex) {
                        throw new CustomerNotFoundException(customerId);
                    }
                },
                throwable -> {
                    if (throwable instanceof CustomerNotFoundException) {
                        throw (CustomerNotFoundException) throwable;
                    }
                    throw new ServiceUnavailableException("Customer Service is currently unavailable");
                }
        );
    }

    public ProductResponse getProduct(Long productId) {
        return productCircuitBreaker.run(
                () -> productClient.getProductById(productId),
                throwable -> {
                    throw new ServiceUnavailableException("Product Service is currently unavailable");
                }
        );
    }

    public boolean checkStock(Long productId, int quantity) {
        return productCircuitBreaker.run(
                () -> productClient.checkStock(productId, quantity),
                throwable -> {
                    throw new ServiceUnavailableException("Product Service is currently unavailable");
                }
        );
    }

    public void reduceStock(Long productId, int quantity) {
        productCircuitBreaker.run(
                () -> {
                    productClient.reduceStock(productId, quantity);
                    return null;
                },
                throwable -> {
                    throw new ServiceUnavailableException("Product Service is currently unavailable");
                }
        );
    }
}