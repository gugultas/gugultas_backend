package com.serbest.magazine.backend.service;

import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryActiveListResponseDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryCreateRequestDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryListResponseDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryUpdateRequestDTO;

import java.util.List;

public interface SubCategoryService {

    MessageResponseDTO createSubCategory(SubCategoryCreateRequestDTO subCategoryCreateRequestDTO);

    List<SubCategoryListResponseDTO> getAllSubCategoriesByCategoryName(String categoryName);

    MessageResponseDTO updateSubCategory(String subCategoryId, SubCategoryUpdateRequestDTO subCategoryUpdateRequestDTO);

    MessageResponseDTO deActivateSubCategoryById(String subCategoryId);

    MessageResponseDTO activateSubCategoryById(String subCategoryId);

    List<SubCategoryActiveListResponseDTO> getAllActiveSubCategoriesByCategoryName(String category);
}
