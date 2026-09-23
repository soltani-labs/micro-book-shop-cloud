package com.bookstore.orderservice.dto;

public class OrderItemResponse {

    private Long productId;
    private String productTitle;
    private Integer quantity;
    private Double unitPrice;

    public OrderItemResponse() {}

    public OrderItemResponse(Long productId, String productTitle, Integer quantity, Double unitPrice) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
}