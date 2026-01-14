package com.ecommerce.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.backend.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Find category by name (for validation, avoiding duplicates)
    Optional<Category> findByName(String name);
    
    // Check if category exists by name
    boolean existsByName(String name);
}