package com.serbest.magazine.backend.controller;

import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryActiveListResponseDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryCreateRequestDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryListResponseDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryUpdateRequestDTO;
import com.serbest.magazine.backend.service.SubCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "https://gugultas.com"}, maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/administration/subCategories")
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    public SubCategoryController(SubCategoryService subCategoryService) {
        this.subCategoryService = subCategoryService;
    }


    @PostMapping
    public ResponseEntity<MessageResponseDTO>
    createSubCategory(@Valid @RequestBody SubCategoryCreateRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subCategoryService.createSubCategory(requestDTO));
    }

    @GetMapping("/byCategoryName/{category}")
    public ResponseEntity<List<SubCategoryListResponseDTO>> getAllSubCategoriesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(subCategoryService.getAllSubCategoriesByCategoryName(category));
    }

    @GetMapping("/byCategoryName/activeSubCategories/{category}")
    public ResponseEntity<List<SubCategoryActiveListResponseDTO>> getAllActiveSubCategoriesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(subCategoryService.getAllActiveSubCategoriesByCategoryName(category));
    }

    @PutMapping("/updateSubCategory/{subCategoryId}")
    public ResponseEntity<MessageResponseDTO> updateSubCategory(@PathVariable String subCategoryId
            , @Valid @RequestBody SubCategoryUpdateRequestDTO subCategoryUpdateRequestDTO) {
        return ResponseEntity.ok(subCategoryService.updateSubCategory(subCategoryId, subCategoryUpdateRequestDTO));
    }

    @PutMapping("/activateSubCategory/{subCategoryId}")
    public ResponseEntity<MessageResponseDTO> activateSubCategory(@PathVariable String subCategoryId) {
        return ResponseEntity.ok(subCategoryService.activateSubCategoryById(subCategoryId));
    }

    @PutMapping("/deactivateSubCategory/{subCategoryId}")
    public ResponseEntity<MessageResponseDTO> deactivateSubCategory(@PathVariable String subCategoryId) {
        return ResponseEntity.ok(subCategoryService.deActivateSubCategoryById(subCategoryId));
    }
}
