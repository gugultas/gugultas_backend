package com.serbest.magazine.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryCreateRequestDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryListResponseDTO;
import com.serbest.magazine.backend.dto.subcategory.SubCategoryUpdateRequestDTO;
import com.serbest.magazine.backend.service.SubCategoryService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class SubCategoryControllerTest {

    private final static String CONTENT_TYPE = "application/json";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubCategoryService subCategoryService;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    public void RA_test_createSubCategory_shouldAllowCreationOfSubCategory() {
        SubCategoryCreateRequestDTO requestDTO =
                new SubCategoryCreateRequestDTO("İç Siyaset", "Siyaset");

        Mockito.when(subCategoryService.createSubCategory(requestDTO)).thenReturn(
                new MessageResponseDTO("Successful Response")
        );

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestDTO)
                .when()
                .post("/api/administration/subCategories")
                .then()
                .statusCode(201);
    }

    @Test
    public void RA_test_createSubCategory_return403_whenNotAdmin() {
        SubCategoryCreateRequestDTO requestDTO =
                new SubCategoryCreateRequestDTO("İç Siyaset", "Siyaset");

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("EDITOR"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestDTO)
                .when()
                .post("/api/administration/subCategories")
                .then()
                .statusCode(403);
    }

    @Test
    public void RA_test_createSubCategory_return401_whenNotAuthenticated() {
        SubCategoryCreateRequestDTO requestDTO =
                new SubCategoryCreateRequestDTO("İç Siyaset", "Siyaset");

        RestAssuredMockMvc
                .given()
                .auth().none()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestDTO)
                .when()
                .post("/api/administration/subCategories")
                .then()
                .statusCode(401);
    }

    @Test
    public void RA_test_createSubCategory_return400_whenNameNotValid() {
        SubCategoryCreateRequestDTO requestDTO =
                new SubCategoryCreateRequestDTO("fjkehfkajh kjhdwakjdawkjdg awkjdgawkjdagwdkjaw", "Siyaset");

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestDTO)
                .when()
                .post("/api/administration/subCategories")
                .then()
                .statusCode(400);
    }

    @Test
    public void RA_test_createSubCategory_return400_whenNameNotProvided() {
        SubCategoryCreateRequestDTO requestDTO =
                new SubCategoryCreateRequestDTO("", "Siyaset");

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestDTO)
                .when()
                .post("/api/administration/subCategories")
                .then()
                .statusCode(400);
    }

    @Test
    public void RA_test_createSubCategory_return400_whenCategoryNotProvided() {
        SubCategoryCreateRequestDTO requestDTO =
                new SubCategoryCreateRequestDTO("İç Siyaset", "");

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestDTO)
                .when()
                .post("/api/administration/subCategories")
                .then()
                .statusCode(400);
    }

    @Test
    public void RA_test_getAllSubCategoriesByCategory_allowFetchingSubCategoriesWithoutAuth() {
        SubCategoryListResponseDTO responseDTO = new SubCategoryListResponseDTO(UUID.randomUUID(), "İç Siyaset",true);

        Mockito.when(subCategoryService.getAllSubCategoriesByCategoryName("Siyaset"))
                .thenReturn(List.of(responseDTO));

        RestAssuredMockMvc
                .given()
                .auth().none()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/administration/subCategories/byCategoryName/" + "Siyaset")
                .then()
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(1))
                .body("[0].name", Matchers.equalTo("İç Siyaset"));

    }

    @Test
    public void RA_test_updateSubCategory_shouldAllowUpdateOfSubCategory() {
        UUID subCategoryId = UUID.randomUUID();
        SubCategoryUpdateRequestDTO requestDTO =
                new SubCategoryUpdateRequestDTO("İç Siyaset", "Siyaset");

        Mockito.when(subCategoryService.updateSubCategory(subCategoryId.toString(), requestDTO)).thenReturn(
                new MessageResponseDTO("Successful Response")
        );

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestDTO)
                .when()
                .put("/api/administration/subCategories/updateSubCategory/" + subCategoryId)
                .then()
                .statusCode(200)
                .body("message",Matchers.equalTo("Successful Response"));
    }

    @Test
    public void RA_test_updateSubCategory_return403_whenNotAdmin() {
        UUID subCategoryId = UUID.randomUUID();

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("EDITOR"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("requestDTO")
                .when()
                .put("/api/administration/subCategories/updateSubCategory/" + subCategoryId)
                .then()
                .statusCode(403);
    }

    @Test
    public void RA_test_updateSubCategory_return401_whenNotAuthenticated() {
        UUID subCategoryId = UUID.randomUUID();

        RestAssuredMockMvc
                .given()
                .auth().none()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("requestDTO")
                .when()
                .put("/api/administration/subCategories/updateSubCategory/" + subCategoryId)
                .then()
                .statusCode(401);
    }

    @Test
    public void RA_test_updateSubCategory_return400_whenNameNotValid() {
        UUID subCategoryId = UUID.randomUUID();

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(
                        new SubCategoryUpdateRequestDTO(
                                "adjkakjhdajkdahfjkafjkafa jahsjdhasjdaa jkdgKJSAgAHDGADJAFJK",
                                "Siyaset"))
                .when()
                .put("/api/administration/subCategories/updateSubCategory/" + subCategoryId)
                .then()
                .statusCode(400);
    }

    @Test
    public void RA_test_updateSubCategory_return400_whenNameNotProvided() {
        UUID subCategoryId = UUID.randomUUID();

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new SubCategoryUpdateRequestDTO("", "Siyaset"))
                .when()
                .put("/api/administration/subCategories/updateSubCategory/" + subCategoryId)
                .then()
                .statusCode(400);
    }

    @Test
    public void RA_test_updateSubCategory_return400_whenCategoryNotProvided() {
        UUID subCategoryId = UUID.randomUUID();

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new SubCategoryUpdateRequestDTO("adjkakjhdajkdahfjkafjkafa", ""))
                .when()
                .put("/api/administration/subCategories/updateSubCategory/" + subCategoryId)
                .then()
                .statusCode(400);
    }

    @Test
    public void RA_test_deactivateSubCategory_shouldAllowDeactivateOfSubCategory() {
        UUID subCategoryId = UUID.randomUUID();

        Mockito.when(subCategoryService.deActivateSubCategoryById(subCategoryId.toString())).thenReturn(
                new MessageResponseDTO("Successful Response")
        );

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/api/administration/subCategories/deactivateSubCategory/" + subCategoryId)
                .then()
                .statusCode(200)
                .body("message",Matchers.equalTo("Successful Response"));
    }

    @Test
    public void RA_test_deactivateSubCategory_return403_whenNotAdmin() {
        UUID subCategoryId = UUID.randomUUID();

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("EDITOR"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/api/administration/subCategories/deactivateSubCategory/" + subCategoryId)
                .then()
                .statusCode(403);
    }

    @Test
    public void RA_test_deactivateSubCategory_return401_whenNotAuthenticated() {
        UUID subCategoryId = UUID.randomUUID();

        RestAssuredMockMvc
                .given()
                .auth().none()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/api/administration/subCategories/deactivateSubCategory/" + subCategoryId)
                .then()
                .statusCode(401);
    }

}