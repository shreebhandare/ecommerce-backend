package com.ecommerce.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.backend.dto.AddToCartRequestDTO;
import com.ecommerce.backend.dto.CartResponseDTO;
import com.ecommerce.backend.dto.UpdateCartItemRequestDTO;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    // Helper method to get current logged-in user
    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // POST /api/cart/items - Add item to cart
    @PostMapping("/items")
    public ResponseEntity<CartResponseDTO> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddToCartRequestDTO request) {
        User user = getCurrentUser(userDetails);
        CartResponseDTO response = cartService.addToCart(user, request);
        return ResponseEntity.ok(response);
    }

    // GET /api/cart - View cart
    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        CartResponseDTO response = cartService.getCart(user);
        return ResponseEntity.ok(response);
    }

    // PUT /api/cart/items/{cartItemId} - Update quantity
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponseDTO> updateCartItemQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequestDTO request) {
        User user = getCurrentUser(userDetails);
        CartResponseDTO response = cartService.updateCartItemQuantity(user, cartItemId, request);
        return ResponseEntity.ok(response);
    }

    // DELETE /api/cart/items/{cartItemId} - Remove one item
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponseDTO> removeCartItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId) {
        User user = getCurrentUser(userDetails);
        CartResponseDTO response = cartService.removeCartItem(user, cartItemId);
        return ResponseEntity.ok(response);
    }

    
    // DELETE /api/cart - Clear entire cart
    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }
}