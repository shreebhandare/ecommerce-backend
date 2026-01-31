package com.ecommerce.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.backend.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final OrderService orderService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public WebhookController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        // Verify webhook signature
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            // Invalid signature
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid signature");
        }


/*
        // TEMPORARY: Skip signature verification for local testing
        try {
            event = Event.GSON.fromJson(payload, Event.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid payload");
        }
*/




        // Handle the event
        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);

            if (paymentIntent != null) {
                // Get order ID from metadata
                String orderIdStr = paymentIntent.getMetadata().get("orderId");
                
                if (orderIdStr != null) {
                    try {
                        Long orderId = Long.parseLong(orderIdStr);
                        
                        // Mark order as paid and reduce stock
                        orderService.markOrderAsPaid(orderId);
                        
                        System.out.println("Payment succeeded for order: " + orderId);
                    } catch (Exception e) {
                        System.err.println("Error processing payment: " + e.getMessage());
                        // Return 200 anyway to acknowledge receipt
                    }
                }
            }
        }

        // Return 200 to acknowledge receipt
        return ResponseEntity.ok("Webhook received");
    }
}