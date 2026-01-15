package com.ecommerce.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ecommerce.backend.dto.ProductRequestDTO;
import com.ecommerce.backend.dto.ProductResponseDTO;
import com.ecommerce.backend.entity.Category;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.ecommerce.backend.dto.PagedResponseDTO;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    // Note: This service uses Java Streams for collection transformations.
    // Stream operations: .stream() creates a stream, .map() transforms each element,
    // .collect() gathers results back into a collection.
    public ProductService(ProductRepository productRepository,
                        CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        // Find category
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Category not found with id: " + requestDTO.getCategoryId()));
        
        // Convert DTO to Entity
        Product product = new Product();
        product.setName(requestDTO.getName());
        product.setPrice(requestDTO.getPrice());
        product.setCategory(category);
        product.setStockQuantity(requestDTO.getStockQuantity());
        product.setImageUrl(requestDTO.getImageUrl());
        product.setVideoUrl(requestDTO.getVideoUrl());

        // Save entity
        Product savedProduct = productRepository.save(product);

        // Convert entity back to Response DTO
        return convertToProductResponseDTO(savedProduct);
    }

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToProductResponseDTO)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return convertToProductResponseDTO(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }


/*
    public ProductResponseDTO findByNameContainingIgnoreCase(String name){
        Product product = productRepository.findByNameContainingIgnoreCase(name)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found with name: " + id));
        return new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getPrice()
        );
    }

*/

    private ProductResponseDTO convertToProductResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getStockQuantity(),
                product.getImageUrl(),
                product.getVideoUrl()
        );
    }

    // Get products with pagination
    public PagedResponseDTO<ProductResponseDTO> getProducts(int page, int size, String sortBy, String sortDir) {
        // Create sort object
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        // Create pageable object
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Get paginated products
        Page<Product> productPage = productRepository.findAll(pageable);
        
        // Convert to DTOs
        List<ProductResponseDTO> productDTOs = productPage.getContent().stream()
                .map(this::convertToProductResponseDTO)
                .collect(Collectors.toList());
        
        // Build paginated response
        return new PagedResponseDTO<>(
                productDTOs,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isFirst(),
                productPage.isLast()
        );
    }
}