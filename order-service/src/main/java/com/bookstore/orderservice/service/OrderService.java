package com.bookstore.orderservice.service;

import com.bookstore.orderservice.client.CustomerClient;
import com.bookstore.orderservice.client.ProductClient;
import com.bookstore.orderservice.dto.*;
import com.bookstore.orderservice.entity.Order;
import com.bookstore.orderservice.entity.OrderItem;
import com.bookstore.orderservice.entity.OrderStatus;
import com.bookstore.orderservice.exception.CustomerNotFoundException;
import com.bookstore.orderservice.exception.InsufficientStockException;
import com.bookstore.orderservice.exception.OrderNotFoundException;
import com.bookstore.orderservice.repository.OrderRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ResilientClientService resilientClientService;

    public OrderService(OrderRepository orderRepository, ResilientClientService resilientClientService) {
        this.orderRepository = orderRepository;
        this.resilientClientService = resilientClientService;
    }

    public OrderResponse placeOrder(OrderRequest request) {
        resilientClientService.validateCustomerExists(request.getCustomerId());

        Order order = new Order(request.getCustomerId());
        double total = 0.0;

        for (OrderItemRequest itemRequest : request.getItems()) {
            ProductResponse product = resilientClientService.getProduct(itemRequest.getProductId());

            boolean inStock = resilientClientService.checkStock(itemRequest.getProductId(), itemRequest.getQuantity());
            if (!inStock) {
                throw new InsufficientStockException(itemRequest.getProductId());
            }

            OrderItem item = new OrderItem(
                    product.getId(),
                    product.getTitle(),
                    itemRequest.getQuantity(),
                    product.getPrice()
            );
            order.addItem(item);

            total += product.getPrice() * itemRequest.getQuantity();

            resilientClientService.reduceStock(itemRequest.getProductId(), itemRequest.getQuantity());
        }

        order.setTotalAmount(total);
        order.setStatus(OrderStatus.CONFIRMED);

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return toResponse(order);
    }

    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        return orderRepository.findAll()
                .stream()
                .filter(o -> o.getCustomerId().equals(customerId))
                .map(this::toResponse)
                .toList();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(i -> new OrderItemResponse(i.getProductId(), i.getProductTitle(), i.getQuantity(), i.getUnitPrice()))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getTotalAmount(),
                itemResponses
        );
    }
}