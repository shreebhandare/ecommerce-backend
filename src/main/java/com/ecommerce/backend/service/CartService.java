package com.ecommerce.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.entity.CartItem;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.CartItemRepository;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.ProductRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.ecommerce.backend.dto.AddToCartRequestDTO;
import com.ecommerce.backend.dto.CartItemResponseDTO;
import com.ecommerce.backend.dto.CartResponseDTO;
import com.ecommerce.backend.dto.UpdateCartItemRequestDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;  // ← Add this

    public CartService(CartRepository cartRepository,
                      CartItemRepository cartItemRepository,
                      ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }
    // Methods will go here

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
            .orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUser(user);
                return cartRepository.save(newCart);
            });
    }


    @Transactional
    public CartResponseDTO addToCart(User user, AddToCartRequestDTO request) {
        // Step 1: Get or create user's cart
        Cart cart = getOrCreateCart(user);
        
        // Step 2: Find the product (or fail if doesn't exist)
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Product not found with id: " + request.getProductId()));
        
        // Step 2.1: Check if product already in cart
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElse(null);

        // Step 2.2: Calculate total quantity (existing + new)
        int existingQuantity = (cartItem != null) ? cartItem.getQuantity() : 0;
        int totalQuantity = existingQuantity + request.getQuantity();

        // Step 2.3: Validate total quantity against stock
        if (product.getStockQuantity() < totalQuantity) {
            throw new RuntimeException(
                "Insufficient stock. Available: " + product.getStockQuantity() + 
                ", Already in cart: " + existingQuantity + 
                ", Requested: " + request.getQuantity()
            );
        }

        // Step 3: Update or create cart item
        if (cartItem != null) {
            // Product exists in cart - update quantity
            cartItem.setQuantity(totalQuantity);
        } else {
            // Product not in cart - create new cart item
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPriceAtAdd(product.getPrice());
            cart.getItems().add(cartItem);
        }
        
        // Step 4: Save the cart item
        cartItemRepository.save(cartItem);
        
        // Step 5: Convert to DTO and return
        return convertToCartResponseDTO(cart);
    }

    private CartResponseDTO convertToCartResponseDTO(Cart cart) {
        // Convert each CartItem to CartItemResponseDTO
        List<CartItemResponseDTO> itemDTOs = cart.getItems().stream()
                .map(item -> {
                    double subtotal = item.getPriceAtAdd() * item.getQuantity();
                    return new CartItemResponseDTO(
                            item.getId(),
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            item.getPriceAtAdd(),
                            item.getQuantity(),
                            subtotal
                    );
                })
                .collect(Collectors.toList());
        
        // Calculate total amount (sum of all subtotals)
        double totalAmount = itemDTOs.stream()
                .mapToDouble(CartItemResponseDTO::getSubtotal)
                .sum();
        
        // Calculate total items count
        int totalItems = itemDTOs.stream()
                .mapToInt(CartItemResponseDTO::getQuantity)
                .sum();
        
        // Build and return CartResponseDTO
        return new CartResponseDTO(
                cart.getId(),
                cart.getUser().getId(),
                cart.getUser().getUsername(),
                itemDTOs,
                totalAmount,
                totalItems
        );
    }

    public CartResponseDTO getCart(User user) {
        // Get or create cart (reuse our helper method!)
        Cart cart = getOrCreateCart(user);        
        return convertToCartResponseDTO(cart);
    }
    
    @Transactional
    public CartResponseDTO updateCartItemQuantity(User user, Long cartItemId, 
                                                UpdateCartItemRequestDTO request) {
        // Step 1: Find the cart item
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Cart item not found with id: " + cartItemId));
        
        // Step 2: Verify ownership (security check) - same error message
        if (cartItem.getCart().getUser().getId() != user.getId()) {
            throw new ResourceNotFoundException(
                "Cart item not found with id: " + cartItemId);
        }
        
        // Step 3: Update quantity
        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        
        // Step 4: Return updated cart
        return convertToCartResponseDTO(cartItem.getCart());
    }
/*
    @Transactional
    public CartResponseDTO removeCartItem(User user, Long cartItemId) {
        // Step 1: Find the cart item
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Cart item not found with id: " + cartItemId));
        // Step 2: Verify ownership (security check) - same error message
        if (cartItem.getCart().getUser().getId() != user.getId()) {
            throw new ResourceNotFoundException("Cart item not found with id: " + cartItemId);}        
        // Step 3: Get the cart before deleting item (we need it for response)
        Cart cart = cartItem.getCart();
        // Step 4:Remove item from cart's collection first (important!)
        cart.getItems().remove(cartItem);
        // Step 5: Delete the cart item
        cartItemRepository.delete(cartItem);
        // Step 6: Return updated cart
        return convertToCartResponseDTO(cart);
    }

*/

    @Transactional
    public CartResponseDTO removeCartItem(User user, Long cartItemId) {
        // Step 1: Find the cart item
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Cart item not found with id: " + cartItemId));
        
        // Step 2: Verify ownership
        if (cartItem.getCart().getUser().getId() != user.getId()) {
            throw new ResourceNotFoundException(
                "Cart item not found with id: " + cartItemId);
        }
        
        // Step 3: Get cart ID before deletion
        Long cartId = cartItem.getCart().getId();
        
        // Step 4: Delete the cart item
        cartItemRepository.deleteById(cartItemId);
        
        // Step 5: Force JPA to execute the delete NOW
        entityManager.flush();
        
        // Step 6: Clear the persistence context to force fresh fetch
        entityManager.clear();
        
        // Step 7: Fetch fresh cart from database
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        
        // Step 8: Return updated cart
        return convertToCartResponseDTO(cart);
    }
    @Transactional
    public void clearCart(User user) {
        // Step 1: Get or create cart (if user has no cart, nothing to clear)
        Cart cart = getOrCreateCart(user);
        
        // Step 2: Delete all items from the cart
        cartItemRepository.deleteAll(cart.getItems());
        
        // Step 3: Clear the items list in memory (keeps entity in sync)
        cart.getItems().clear();
    }
}