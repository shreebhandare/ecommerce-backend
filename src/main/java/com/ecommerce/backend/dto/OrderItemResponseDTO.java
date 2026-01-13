package com.ecommerce.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Double priceAtOrder;  // Price snapshot
    private Integer quantity;
    private Double subtotal;      // priceAtOrder * quantity
}