package com.ecommerce.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Paginated version
    Page<Order> findByUser(User user, Pageable pageable);
    
    // Non-paginated version (keep for backward compatibility)
    List<Order> findByUser(User user);
    
    // Find specific order by ID and user (security check)
    Optional<Order> findByIdAndUser(Long id, User user);
}