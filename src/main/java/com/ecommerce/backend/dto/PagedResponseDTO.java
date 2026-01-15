package com.ecommerce.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponseDTO<T> {
    
    private List<T> content;          // The actual data
    private int currentPage;          // Current page number (0-indexed)
    private int pageSize;             // Items per page
    private long totalElements;       // Total items across all pages
    private int totalPages;           // Total number of pages
    private boolean first;            // Is this the first page?
    private boolean last;             // Is this the last page?
}