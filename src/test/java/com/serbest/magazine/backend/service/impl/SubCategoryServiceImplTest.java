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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubCategoryServiceImplTest {

    @InjectMocks
    SubCategoryServiceImpl subCategoryService;

    @Mock
    SubCategoryRepository subCategoryRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Test
    public void test_createSubCategory_withSuccess(){
        Category category = mock(Category.class);
        SubCategory subCategory = mock(SubCategory.class);
        SubCategoryCreateRequestDTO requestDTO = new SubCategoryCreateRequestDTO("İç Siyaset","Siyaset");

        when(subCategory.getName()).thenReturn(requestDTO.getName());

        when(categoryRepository.findByName("Siyaset")).thenReturn(Optional.of(category));
        when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(subCategory);

        MessageResponseDTO responseDTO = subCategoryService.createSubCategory(requestDTO);

        assertEquals(responseDTO.getMessage(),"New Sub Category named İç Siyaset is created.");

    }

    @Test
    public void test_createSubCategory_NameNotProvided(){
        assertThrows(
                IllegalArgumentException.class,
                () -> subCategoryService.createSubCategory(
                        new SubCategoryCreateRequestDTO("","Siyaset"))
        );
    }

    @Test
    public void test_createSubCategory_CategoryNotProvided(){
        assertThrows(
                IllegalArgumentException.class,
                () -> subCategoryService.createSubCategory(
                        new SubCategoryCreateRequestDTO("İç Siyaset",""))
        );
    }

    @Test
    public void test_createSubCategory_CategoryNotFound(){
        assertThrows(
                ResourceNotFoundException.class,
                () -> subCategoryService.createSubCategory(
                        new SubCategoryCreateRequestDTO("İç Siyaset","Siyaset"))
        );
    }

    @Test
    public void test_createSubCategory_NameExceededRange(){
        assertThrows(
                IllegalArgumentException.class,
                () -> subCategoryService.createSubCategory(
                        new SubCategoryCreateRequestDTO("ExampleSubCategoryNameMoreThanTwentyCharacters"
                                ,"Siyaset"))
        );
    }

    @Test
    public void test_createSubCategory_customError(){
        Category category = mock(Category.class);

        when(categoryRepository.findByName("Siyaset")).thenReturn(Optional.of(category));

        assertThrows(
                CustomApplicationException.class,
                () -> subCategoryService.createSubCategory(
                        new SubCategoryCreateRequestDTO("İç Siyaset","Siyaset")
                )

        );
    }

    @Test
    public void test_getAllSubCategoriesByCategoryName_withSuccess(){

        Category category = mock(Category.class);

        SubCategory subCategory = new SubCategory("İç siyaset",category);
        when(categoryRepository.findByName("Siyaset")).thenReturn(Optional.of(category));
        when(subCategoryRepository.findAllByCategoryName("Siyaset")).thenReturn(List.of(subCategory));

        List<SubCategoryListResponseDTO> responseDTOS = subCategoryService.getAllSubCategoriesByCategoryName("Siyaset");

        assertEquals(1,responseDTOS.size());
        assertEquals("İç siyaset",responseDTOS.get(0).getName());

    }

    @Test
    public void test_updateSubCategory_withSuccess(){
        UUID subCategoryId = UUID.randomUUID();
        Category category = mock(Category.class);
        SubCategory subCategory = mock(SubCategory.class);

        when(category.getName()).thenReturn("Siyaset");
        when(categoryRepository.findByName("Siyaset")).thenReturn(Optional.of(category));
        when(subCategoryRepository.findById(subCategoryId)).thenReturn(Optional.of(subCategory));
        when(subCategory.getName()).thenReturn("İç siyaset");

        when(subCategoryRepository.save(any(SubCategory.class)))
                .thenReturn(new SubCategory("İç Siyaset",category));

        MessageResponseDTO responseDTO = subCategoryService.updateSubCategory(
                subCategoryId.toString(),new SubCategoryUpdateRequestDTO("İç Siyaset","Siyaset")
        );

        assertEquals(
                "The SubCategory with name İç siyaset updated " +
                        "with new name İç Siyaset ,and new category become : Siyaset."
                ,responseDTO.getMessage());

    }

    @Test
    public void test_updateSubCategory_IdNotProvided(){
        assertThrows(
                IllegalArgumentException.class,
                () -> subCategoryService.updateSubCategory(null,
                        new SubCategoryUpdateRequestDTO("İç Siyaset","Siyaset"))
        );
    }

    @Test
    public void test_updateSubCategory_NameNotProvided(){
        assertThrows(
                IllegalArgumentException.class,
                () -> subCategoryService.updateSubCategory(UUID.randomUUID().toString(),
                        new SubCategoryUpdateRequestDTO("","Siyaset"))
        );
    }

    @Test
    public void test_updateSubCategory_CategoryNotProvided(){
        assertThrows(
                IllegalArgumentException.class,
                () -> subCategoryService.updateSubCategory(UUID.randomUUID().toString(),
                        new SubCategoryUpdateRequestDTO("İç Siyaset",""))
        );
    }

    @Test
    public void test_updateSubCategory_NameExceededRange(){
        assertThrows(
                IllegalArgumentException.class,
                () -> subCategoryService.updateSubCategory(UUID.randomUUID().toString(),
                        new SubCategoryUpdateRequestDTO("ExampleSubCategoryNameMoreThanTwentyCharacters"
                                ,"Siyaset"))
        );
    }

    @Test
    public void test_updateSubCategory_CategoryNotFound(){
        assertThrows(
                ResourceNotFoundException.class,
                () -> subCategoryService.updateSubCategory(UUID.randomUUID().toString(),
                        new SubCategoryUpdateRequestDTO("İç Siyaset","Siyaset"))
        );
    }

    @Test
    public void test_updateSubCategory_SubCategoryNotFound(){
        Category category = mock(Category.class);
        when(categoryRepository.findByName("Siyaset")).thenReturn(Optional.of(category));

        assertThrows(
                ResourceNotFoundException.class,
                () -> subCategoryService.updateSubCategory(UUID.randomUUID().toString(),
                        new SubCategoryUpdateRequestDTO("İç Siyaset","Siyaset"))
        );
    }

    @Test
    public void test_updateSubCategory_WithoutSave(){
        UUID subCategoryId = UUID.randomUUID();

        SubCategory subCategory = mock(SubCategory.class);
        Category category = mock(Category.class);

        when(categoryRepository.findByName("Siyaset")).thenReturn(Optional.of(category));
        when(subCategoryRepository.findById(subCategoryId)).thenReturn(Optional.of(subCategory));

        assertThrows(
                CustomApplicationException.class,
                () -> subCategoryService.updateSubCategory(subCategoryId.toString(),
                        new SubCategoryUpdateRequestDTO("İç Siyaset","Siyaset"))
        );
    }

    @Test
    public void test_deActivateSubCategoryById_withSuccess(){
        UUID subCategoryId = UUID.randomUUID();
        SubCategory subCategory = mock(SubCategory.class);

        when(subCategory.getName()).thenReturn("İç Siyaset");
        when(subCategoryRepository.findById(subCategoryId)).thenReturn(Optional.of(subCategory));

        when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(subCategory);

        MessageResponseDTO responseDTO = subCategoryService.deActivateSubCategoryById(subCategoryId.toString());

        assertEquals("Sub Category named İç Siyaset is deactivated.",responseDTO.getMessage());



    }

    @Test
    public void test_deActivateSubCategoryById_IdNotProvided(){
        assertThrows(
                IllegalArgumentException.class,
                () -> subCategoryService.deActivateSubCategoryById(null)
        );
    }

    @Test
    public void test_deActivateSubCategoryById_SubCategoryNotFound(){
        assertThrows(
                ResourceNotFoundException.class,
                () -> subCategoryService.deActivateSubCategoryById(UUID.randomUUID().toString())
        );
    }

    @Test
    public void test_deActivateSubCategoryById_WithoutSave(){
        UUID subCategoryId = UUID.randomUUID();

        SubCategory subCategory = mock(SubCategory.class);

        when(subCategoryRepository.findById(subCategoryId)).thenReturn(Optional.of(subCategory));

        assertThrows(
                CustomApplicationException.class,
                () -> subCategoryService.deActivateSubCategoryById(subCategoryId.toString())
        );
    }

}