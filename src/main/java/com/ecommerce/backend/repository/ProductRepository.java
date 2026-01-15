package com.ecommerce.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.backend.entity.Category;
import com.ecommerce.backend.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Paginated version of findAll (built-in from JpaRepository)
    Page<Product> findAll(Pageable pageable);
    
    // Find products by category (paginated)
    Page<Product> findByCategory(Category category, Pageable pageable);
    
    // Find products by category ID (paginated)
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    // Non-paginated versions (keep for backward compatibility)
    List<Product> findByCategory(Category category);
    List<Product> findByCategoryId(Long categoryId);
}