package com.serbest.magazine.backend.service.impl;

import com.google.common.base.Strings;
import com.serbest.magazine.backend.common.validation.StringValidationCommon;
import com.serbest.magazine.backend.service.SubCategoryService;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryActiveListResponseDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryCreateRequestDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryListResponseDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryUpdateRequestDTO;
import com.serbest.magazine.backend.entity.Category;
import com.serbest.magazine.backend.entity.SubCategory;
import com.serbest.magazine.backend.exception.CustomApplicationException;
import com.serbest.magazine.backend.exception.ResourceNotFoundException;
import com.serbest.magazine.backend.repository.CategoryRepository;
import com.serbest.magazine.backend.repository.SubCategoryRepository;
import io.jsonwebtoken.lang.Assert;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;

    public SubCategoryServiceImpl(SubCategoryRepository subCategoryRepository, CategoryRepository categoryRepository) {
        this.subCategoryRepository = subCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public MessageResponseDTO createSubCategory(SubCategoryCreateRequestDTO subCategoryCreateRequestDTO) {
        checkValidateAndSanitizeInput("Name", subCategoryCreateRequestDTO.getName());
        checkValidateAndSanitizeInput("Category", subCategoryCreateRequestDTO.getCategoryName());
        StringValidationCommon.common_validateStringLength(0, 25, subCategoryCreateRequestDTO.getName());

        Category category = categoryRepository.findByName(subCategoryCreateRequestDTO.getCategoryName())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category", "categoryName", subCategoryCreateRequestDTO
                                .getCategoryName())
                );

        try {
            SubCategory subCategoryNew = new SubCategory(subCategoryCreateRequestDTO.getName(), category);

            subCategoryNew.setActive(true);
            SubCategory subCategoryCreated = subCategoryRepository.save(subCategoryNew);

            return new MessageResponseDTO("New Sub Category named " + subCategoryCreated.getName() + " is created.");

        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    @Override
    public List<SubCategoryListResponseDTO> getAllSubCategoriesByCategoryName(String categoryName) {
        checkValidateAndSanitizeInput("Category Name", categoryName);

        categoryRepository.findByName(categoryName).orElseThrow(
                () -> new ResourceNotFoundException("Category", "categoryName", categoryName));

        List<SubCategory> subCategories = subCategoryRepository.findAllByCategoryName(categoryName);

        return subCategories
                .stream()
                .map(subCategory -> new SubCategoryListResponseDTO(
                        subCategory.getId(),
                        subCategory.getName(),
                        subCategory.getActive()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SubCategoryActiveListResponseDTO> getAllActiveSubCategoriesByCategoryName(String categoryName) {
        checkValidateAndSanitizeInput("Category Name", categoryName);

        categoryRepository.findByName(categoryName).orElseThrow(
                () -> new ResourceNotFoundException("Category", "categoryName", categoryName));

        List<SubCategory> subCategories = subCategoryRepository.findAllByCategoryNameAndActiveTrue(categoryName);

        return subCategories
                .stream()
                .map(subCategory -> new SubCategoryActiveListResponseDTO(
                        subCategory.getId(),
                        subCategory.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponseDTO updateSubCategory(String subCategoryId, SubCategoryUpdateRequestDTO subCategoryUpdateRequestDTO) {
        checkValidateAndSanitizeInput("SubCategory Id", subCategoryId);
        checkValidateAndSanitizeInput("SubCategory Name", subCategoryUpdateRequestDTO.getName());
        checkValidateAndSanitizeInput("Category Name", subCategoryUpdateRequestDTO.getCategoryName());
        StringValidationCommon.common_validateStringLength(0, 25, subCategoryUpdateRequestDTO.getName());

        Category category = categoryRepository.findByName(subCategoryUpdateRequestDTO.getCategoryName())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category", "categoryName",
                                subCategoryUpdateRequestDTO.getCategoryName())
                );

        SubCategory subCategory = subCategoryRepository.findById(UUID.fromString(subCategoryId))
                .orElseThrow(
                        () -> new ResourceNotFoundException("Sub Category", "id", subCategoryId)
                );

        String oldName = subCategory.getName();

        try {
            subCategory.setCategory(category);
            subCategory.setName(subCategoryUpdateRequestDTO.getName());
            SubCategory updatedSubCategory = subCategoryRepository.save(subCategory);
            return new MessageResponseDTO("The SubCategory with name " + oldName
                    + " updated with new name " + updatedSubCategory.getName() + " ,and new category become : " +
                    updatedSubCategory.getCategory().getName() + "."
            );
        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, e.getMessage());
        }


    }

    @Override
    public MessageResponseDTO activateSubCategoryById(String subCategoryId) {
        checkValidateAndSanitizeInput("SubCategory Id", subCategoryId);

        SubCategory subCategory = subCategoryRepository
                .findById(UUID.fromString(subCategoryId))
                .orElseThrow(
                        () -> new ResourceNotFoundException("Sub Category", "id", subCategoryId)
                );

        try {
            subCategory.setActive(true);
            SubCategory activatedSubCategory = subCategoryRepository.save(subCategory);
            return new MessageResponseDTO("Sub Category named " + activatedSubCategory.getName() + " is activated.");
        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public MessageResponseDTO deActivateSubCategoryById(String subCategoryId) {
        checkValidateAndSanitizeInput("SubCategory Id", subCategoryId);

        SubCategory subCategory = subCategoryRepository
                .findById(UUID.fromString(subCategoryId))
                .orElseThrow(
                        () -> new ResourceNotFoundException("Sub Category", "id", subCategoryId)
                );

        try {
            subCategory.setActive(false);
            SubCategory deActivatedSubCategory = subCategoryRepository.save(subCategory);
            return new MessageResponseDTO("Sub Category named " + deActivatedSubCategory.getName() + " is deactivated.");
        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private void checkValidateAndSanitizeInput(String fieldName, String fieldValue) {
        Assert.notNull(fieldValue, "Please , provide a valid " + fieldName + ".");
        if (Strings.isNullOrEmpty(fieldValue)) {
            throw new IllegalArgumentException("Please , provide a valid " + fieldName + ".");
        }
    }
}
