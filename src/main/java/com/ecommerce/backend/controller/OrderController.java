package com.ecommerce.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import com.ecommerce.backend.dto.PagedResponseDTO;
import com.ecommerce.backend.dto.OrderResponseDTO;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    // Helper method to get current logged-in user
    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // POST /api/orders - Place order (checkout)
    @PostMapping
    public ResponseEntity<OrderResponseDTO> placeOrder(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        OrderResponseDTO response = orderService.placeOrder(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/orders - Get order history
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrderHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        List<OrderResponseDTO> response = orderService.getOrderHistory(user);
        return ResponseEntity.ok(response);
    }

    // GET /api/orders/{orderId} - Get specific order
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        User user = getCurrentUser(userDetails);
        OrderResponseDTO response = orderService.getOrderById(user, orderId);
        return ResponseEntity.ok(response);
    }

    // PATCH /api/orders/{orderId}/cancel - Cancel order
    // @PatchMapping("/{orderId}/cancel")
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        User user = getCurrentUser(userDetails);
        OrderResponseDTO response = orderService.cancelOrder(user, orderId);
        return ResponseEntity.ok(response);
    }

    // GET /api/orders/paged - Get order history with pagination
    @GetMapping("/paged")
    public ResponseEntity<PagedResponseDTO<OrderResponseDTO>> getOrderHistoryPaged(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        User user = getCurrentUser(userDetails);
        PagedResponseDTO<OrderResponseDTO> response = 
                orderService.getOrderHistory(user, page, size);
        
        return ResponseEntity.ok(response);
    }
}