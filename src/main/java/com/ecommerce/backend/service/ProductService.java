package com.ecommerce.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ecommerce.backend.dto.ProductRequestDTO;
import com.ecommerce.backend.dto.ProductResponseDTO;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    // Note: This service uses Java Streams for collection transformations.
    // Stream operations: .stream() creates a stream, .map() transforms each element,
    // .collect() gathers results back into a collection.
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        // Convert DTO to Entity
        Product product = new Product();
        product.setName(requestDTO.getName());
        product.setPrice(requestDTO.getPrice());

        // Save entity
        Product savedProduct = productRepository.save(product);

        // Convert entity back to Response DTO
        return new ProductResponseDTO(
            savedProduct.getId(),
            savedProduct.getName(),
            savedProduct.getPrice()
        );
    }

    public List<ProductResponseDTO> getAllProducts(){
        // Get all Product entities from database
        // Convert the list to a stream, transform each Product to ProductResponseDTO,
        // then collect all DTOs back into a List
        return productRepository.findAll().stream()
        .map(product -> new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getPrice())
        )
        .collect(Collectors.toList());
    }

    public ProductResponseDTO getProductById(Long id){
        Product product = productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getPrice()
        );
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


}