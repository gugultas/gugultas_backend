package com.serbest.magazine.backend.dto.subcategory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryActiveListResponseDTO {
    private UUID id;
    private String name;
}
