package com.ecommerce.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.backend.dto.CategoryRequestDTO;
import com.ecommerce.backend.dto.CategoryResponseDTO;
import com.ecommerce.backend.entity.Category;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.repository.ProductRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository,
                          ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    // Create category
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO) {
        // Check if category name already exists
        if (categoryRepository.existsByName(requestDTO.getName())) {
            throw new RuntimeException("Category with name '" + requestDTO.getName() + "' already exists");
        }

        // Create category
        Category category = new Category();
        category.setName(requestDTO.getName());
        category.setDescription(requestDTO.getDescription());
        category.setImageUrl(requestDTO.getImageUrl());

        Category savedCategory = categoryRepository.save(category);

        return convertToCategoryResponseDTO(savedCategory);
    }

    // Get all categories
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToCategoryResponseDTO)
                .collect(Collectors.toList());
    }

    // Get category by ID
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return convertToCategoryResponseDTO(category);
    }

    // Update category
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Check if new name conflicts with existing category
        if (!category.getName().equals(requestDTO.getName()) && 
            categoryRepository.existsByName(requestDTO.getName())) {
            throw new RuntimeException("Category with name '" + requestDTO.getName() + "' already exists");
        }

        category.setName(requestDTO.getName());
        category.setDescription(requestDTO.getDescription());
        category.setImageUrl(requestDTO.getImageUrl());

        Category updatedCategory = categoryRepository.save(category);

        return convertToCategoryResponseDTO(updatedCategory);
    }

    // Delete category
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Check if category has products
        long productCount = productRepository.findByCategoryId(id).size();
        if (productCount > 0) {
            throw new RuntimeException("Cannot delete category with " + productCount + " products. Reassign or delete products first.");
        }

        categoryRepository.delete(category);
    }

    // Helper method to convert entity to DTO
    private CategoryResponseDTO convertToCategoryResponseDTO(Category category) {
        int productCount = productRepository.findByCategoryId(category.getId()).size();
        
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getImageUrl(),
                productCount
        );
    }
}