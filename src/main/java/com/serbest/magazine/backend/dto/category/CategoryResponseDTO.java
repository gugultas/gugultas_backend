package com.serbest.magazine.backend.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {
    private UUID id;
    private String name;
    private Integer postCounts;
}
