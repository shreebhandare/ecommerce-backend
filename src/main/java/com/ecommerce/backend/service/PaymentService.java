package com.ecommerce.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.backend.dto.PaymentIntentRequestDTO;
import com.ecommerce.backend.dto.PaymentIntentResponseDTO;
import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.OrderStatus;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.OrderRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public PaymentService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Create payment intent for an order
    @Transactional
    public PaymentIntentResponseDTO createPaymentIntent(User user, PaymentIntentRequestDTO request) {
        // Set Stripe API key
        Stripe.apiKey = stripeApiKey;

        // Find order and verify ownership
        Order order = orderRepository.findByIdAndUser(request.getOrderId(), user)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Order not found with id: " + request.getOrderId()));

        // Verify order is in PENDING status
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order is not in PENDING status. Current status: " + order.getStatus());
        }

        try {
            // Convert amount to cents (Stripe requires smallest currency unit)
            long amountInCents = (long) (order.getTotalAmount() * 100);

            // Create payment intent parameters
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency("usd")
                    .putMetadata("orderId", String.valueOf(order.getId()))
                    .putMetadata("userId", String.valueOf(user.getId()))
                    .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                            .setEnabled(true)
                            .build()
                    )
                    .build();

            // Create payment intent with Stripe
            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Return response for frontend
            return new PaymentIntentResponseDTO(
                    paymentIntent.getClientSecret(),
                    order.getId(),
                    order.getTotalAmount(),
                    "usd",
                    paymentIntent.getStatus()
            );

        } catch (StripeException e) {
            throw new RuntimeException("Failed to create payment intent: " + e.getMessage());
        }
    }
}