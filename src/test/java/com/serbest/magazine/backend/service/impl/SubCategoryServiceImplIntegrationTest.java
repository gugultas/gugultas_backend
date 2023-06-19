package com.serbest.magazine.backend.service.impl;

import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryCreateRequestDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryListResponseDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryUpdateRequestDTO;
import com.serbest.magazine.backend.entity.Category;
import com.serbest.magazine.backend.entity.SubCategory;
import com.serbest.magazine.backend.exception.CustomApplicationException;
import com.serbest.magazine.backend.exception.ResourceNotFoundException;
import com.serbest.magazine.backend.repository.CategoryRepository;
import com.serbest.magazine.backend.repository.SubCategoryRepository;
import com.serbest.magazine.backend.service.SubCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class SubCategoryServiceImplIntegrationTest {

    @Autowired
    SubCategoryService subCategoryService;

    @Autowired
    SubCategoryRepository subCategoryRepository;

    @Autowired
    CategoryRepository categoryRepository;

    UUID categoryId;

    UUID subCategoryId;

    @BeforeEach
    void setupCategory(){
        Category category = categoryRepository.save(new Category(UUID.randomUUID(),"Siyaset",true));
        this.categoryId = category.getId();

        SubCategory subCategory = subCategoryRepository.save(new SubCategory(UUID.randomUUID(),"İç Siyaset",category,true));

        this.subCategoryId = subCategory.getId();
    }

    @Test
    public void testIntegration_createSubCategory_withSuccess(){
        SubCategoryCreateRequestDTO requestDTO =
                new SubCategoryCreateRequestDTO("Osmanlı Siyaseti","Siyaset");

        MessageResponseDTO responseDTO = subCategoryService.createSubCategory(requestDTO);

        assertEquals("New Sub Category named Osmanlı Siyaseti is created.",responseDTO.getMessage());
    }

    @Test
    public void testIntegration_createSubCategory_categoryNotFound(){
        SubCategoryCreateRequestDTO requestDTO =
                new SubCategoryCreateRequestDTO("İç Siyaset","notExistedCategory");

        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () -> subCategoryService.createSubCategory(requestDTO)
        );

        assertEquals("Category not found with categoryName : 'notExistedCategory'",exception.getMessage());
    }

    @Test
    public void testIntegration_createSubCategory_notAllowedWithExistedName(){
        SubCategoryCreateRequestDTO requestDTO =
                new SubCategoryCreateRequestDTO("İç Siyaset","Siyaset");

        assertThrows(
                CustomApplicationException.class,
                () -> subCategoryService.createSubCategory(requestDTO)
        );
}

    @Test
    public void testIntegration_getAllSubCategoriesByCategoryName_withSuccess(){

        List<SubCategoryListResponseDTO> responseDTOS = subCategoryService.getAllSubCategoriesByCategoryName("Siyaset");

        assertEquals(1,responseDTOS.size());
        assertEquals("İç Siyaset",responseDTOS.get(0).getName());
    }

    @Test
    public void testIntegration_getAllSubCategoriesByCategoryName_categoryNotFound(){
        assertThrows(
                ResourceNotFoundException.class,
                () -> subCategoryService.getAllSubCategoriesByCategoryName("notExistedCategory")
        );
    }

    @Test
    public void testIntegration_updateSubCategory_withSuccess(){
        SubCategoryUpdateRequestDTO requestDTO =
                new SubCategoryUpdateRequestDTO("Dış Siyaset","Siyaset");

        MessageResponseDTO responseDTO =
                subCategoryService.updateSubCategory(this.subCategoryId.toString(),requestDTO);

        assertEquals(
                "The SubCategory with name " +
                        "İç Siyaset updated with new name " +
                        "Dış Siyaset ,and new category become : " +
                        "Siyaset.",responseDTO.getMessage());
    }

    @Test
    public void testIntegration_updateSubCategory_categoryNotFound(){
        SubCategoryUpdateRequestDTO requestDTO =
                new SubCategoryUpdateRequestDTO("Dış Siyaset","notExistedCategory");

        assertThrows(
                ResourceNotFoundException.class,
                () -> subCategoryService.updateSubCategory(this.subCategoryId.toString(),requestDTO)
        );
    }

    @Test
    public void testIntegration_updateSubCategory_subCategoryNotFound(){
        UUID wrongId = UUID.randomUUID();
        SubCategoryUpdateRequestDTO requestDTO =
                new SubCategoryUpdateRequestDTO("Dış Siyaset","Siyaset");

        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () -> subCategoryService.updateSubCategory(wrongId.toString(),requestDTO)
        );

        assertEquals("Sub Category not found with id : '" + wrongId + "'"
                ,exception.getMessage());
    }

    @Test
    public void testIntegration_deActivateSubCategoryById_withSuccess(){

        MessageResponseDTO responseDTO =
                subCategoryService.deActivateSubCategoryById(this.subCategoryId.toString());

        assertEquals("Sub Category named İç Siyaset is deactivated.",responseDTO.getMessage());
    }

    @Test
    public void testIntegration_deActivateSubCategoryById_subCategoryNotFound(){
        UUID wrongId = UUID.randomUUID();

        Exception exception = assertThrows(
                ResourceNotFoundException.class,
                () -> subCategoryService.deActivateSubCategoryById(wrongId.toString())
        );

        assertEquals("Sub Category not found with id : '" + wrongId + "'"
                ,exception.getMessage());
    }

}