package com.ecommerce.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ecommerce.backend.dto.OrderItemResponseDTO;
import com.ecommerce.backend.dto.OrderResponseDTO;
import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.OrderItem;
import com.ecommerce.backend.entity.OrderStatus;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.OrderItemRepository;
import com.ecommerce.backend.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository,
                       OrderItemRepository orderItemRepository,
                       CartRepository cartRepository,
                       CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
    }

    // Methods will go here
    @Transactional
    public OrderResponseDTO placeOrder(User user){
            
        // Step 1: Get user's cart
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        
        // Step 2: Validate cart is not empty
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot place order with empty cart");
        }
        
        // Step 3: Calculate total amount
        double totalAmount = cart.getItems().stream()
                .mapToDouble(item -> item.getPriceAtAdd() * item.getQuantity())
                .sum();
        
        // Step 4: Create Order entity
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(totalAmount);
        // orderDate is set automatically by @PrePersist
        
        // Step 5: Save Order
        Order savedOrder = orderRepository.save(order);
        
        // Step 6: Create OrderItems from CartItems
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(savedOrder);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPriceAtOrder(cartItem.getPriceAtAdd());
                    return orderItem;
                })
                .collect(Collectors.toList());
        
        // Step 7: Save OrderItems
        orderItemRepository.saveAll(orderItems);
        
        // Step 8: Clear the cart
        cartService.clearCart(user);
        
        // Step 9: Return OrderResponseDTO
        savedOrder.setItems(orderItems);  // Set items for DTO conversion
        return convertToOrderResponseDTO(savedOrder);
    }

    private OrderResponseDTO convertToOrderResponseDTO(Order order) {
        // Convert each OrderItem to OrderItemResponseDTO
        List<OrderItemResponseDTO> itemDTOs = order.getItems().stream()
                .map(item -> {
                    double subtotal = item.getPriceAtOrder() * item.getQuantity();
                    return new OrderItemResponseDTO(
                            item.getId(),
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            item.getPriceAtOrder(),
                            item.getQuantity(),
                            subtotal
                    );
                })
                .collect(Collectors.toList());
        
        // Calculate total items count
        int totalItems = itemDTOs.stream()
                .mapToInt(OrderItemResponseDTO::getQuantity)
                .sum();
        
        // Build and return OrderResponseDTO
        return new OrderResponseDTO(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getUsername(),
                order.getOrderDate(),
                order.getStatus(),
                itemDTOs,
                order.getTotalAmount(),
                totalItems
        );
    }
    public OrderResponseDTO getOrderById(User user, Long orderId) {
        // Find order by ID and user (security check)
        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Order not found with id: " + orderId));
        
        return convertToOrderResponseDTO(order);
    }

    public List<OrderResponseDTO> getOrderHistory(User user) {
        // Find all orders for this user
        List<Order> orders = orderRepository.findByUser(user);
        
        // Convert to DTOs
        return orders.stream()
                .map(this::convertToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponseDTO cancelOrder(User user, Long orderId) {
        // Step 1: Find order and verify ownership
        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Order not found with id: " + orderId));
        
        // Step 2: Check if order can be cancelled (only PENDING orders)
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException(
                "Cannot cancel order with status: " + order.getStatus());
        }
        
        // Step 3: Update status to CANCELLED
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        
        // Step 4: Return updated order
        return convertToOrderResponseDTO(order);
    }
}
