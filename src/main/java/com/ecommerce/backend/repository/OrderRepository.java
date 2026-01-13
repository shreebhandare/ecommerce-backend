package com.ecommerce.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find all orders for a specific user
    List<Order> findByUser(User user);
    
    // Find specific order by ID and user (security check)
    Optional<Order> findByIdAndUser(Long id, User user);
}