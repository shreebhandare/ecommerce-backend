package com.ecommerce.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentResponseDTO {
    
    private String clientSecret;      // For frontend to complete payment
    private Long orderId;
    private Double amount;
    private String currency;
    private String status;
}