package com.ecommerce.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.backend.entity.Category;
import com.ecommerce.backend.entity.Product;


public interface ProductRepository extends JpaRepository<Product, Long> {
    //public ProductResponseDTO findProductByNameIgnoreCase(String name);
    
    // Find products by category
    List<Product> findByCategory(Category category);

    // Find products by category ID
    List<Product> findByCategoryId(Long categoryId);
}
