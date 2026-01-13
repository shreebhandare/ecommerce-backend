package com.ecommerce.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ecommerce.backend.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Long orderId;
    private Long userId;
    private String username;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private List<OrderItemResponseDTO> items;
    private Double totalAmount;
    private Integer totalItems;
}