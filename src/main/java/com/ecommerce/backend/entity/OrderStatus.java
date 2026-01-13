package com.ecommerce.backend.entity;

public enum OrderStatus {
    PENDING,      // Order created, awaiting payment
    PAID,         // Payment received
    PROCESSING,   // Being prepared for shipment
    SHIPPED,      // Sent to customer
    DELIVERED,    // Customer received it
    CANCELLED     // Order cancelled
}