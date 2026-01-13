package com.ecommerce.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
    private Long cartId;
    private Long userId;
    private String username;
    private List<CartItemResponseDTO> items;
    private Double totalAmount;
    private Integer totalItems;
}