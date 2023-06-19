package com.serbest.magazine.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.serbest.magazine.backend.dto.author.AuthorCardResponseDTO;
import com.serbest.magazine.backend.dto.author.AuthorListResponseDTO;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.dto.user.AuthorResponseDTO;
import com.serbest.magazine.backend.dto.user.AuthorUpdateRequestDTO;
import com.serbest.magazine.backend.exception.ResourceNotFoundException;
import com.serbest.magazine.backend.service.AuthorService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    public void RA_test_getOnlyUsers_shouldAllowUsersFetchingWithoutAuthentication() {
        UUID authorId = UUID.randomUUID();
        AuthorResponseDTO responseDTO = AuthorResponseDTO.builder()
                .id(authorId)
                .username("Test")
                .email("test@email.com")
                .createDateTime(LocalDateTime.now())
                .updateDateTime(LocalDateTime.now())
                .build();

        Mockito.when(authorService.getUsers()).thenReturn(List.of(responseDTO));

        RestAssuredMockMvc
                .given()
                .auth().none()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/authors/getUsers")
                .then()
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(1));
    }

    @Test
    public void RA_test_getOnlyAuthors_shouldAllowUsersFetchingWithoutAuthentication() {
        UUID authorId = UUID.randomUUID();
        AuthorListResponseDTO responseDTO = new AuthorListResponseDTO(authorId,"Test User");

        Mockito.when(authorService.getAuthors()).thenReturn(List.of(responseDTO));

        RestAssuredMockMvc
                .given()
                .auth().none()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/authors/getAuthors")
                .then()
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(1));
    }

    @Test
    public void RA_test_getAuthorsForCard_shouldAllowUsersFetchingWithoutAuthentication() {
        UUID authorId = UUID.randomUUID();
        AuthorCardResponseDTO responseDTO = new AuthorCardResponseDTO(authorId,"Test User",null,null,null,null,null);

        Mockito.when(authorService.getAuthorsForCard()).thenReturn(List.of(responseDTO));

        RestAssuredMockMvc
                .given()
                .auth().none()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/authors/getAuthorsForCard")
                .then()
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(1));
    }

    @Test
    public void RA_test_getAuthor_shouldAllowUsersFetchingWithoutAuthentication() {
        UUID authorId = UUID.randomUUID();
        AuthorResponseDTO responseDTO = AuthorResponseDTO.builder()
                .id(authorId)
                .username("Test User")
                .build();

        Mockito.when(authorService.getAuthorByUsername("Test User")).thenReturn(responseDTO);

        RestAssuredMockMvc
                .given()
                .auth().none()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/authors/getUserByUsername/" + "Test User")
                .then()
                .statusCode(200)
                .body("username", Matchers.equalTo("Test User"));
    }

    @Test
    public void RA_test_getAuthor_shouldReturn404UserNotFound() {
        UUID authorId = UUID.randomUUID();
        AuthorResponseDTO responseDTO = AuthorResponseDTO.builder()
                .id(authorId)
                .username("Test User")
                .build();

        Mockito.when(authorService.getAuthorByUsername("Test User")).thenThrow(ResourceNotFoundException.class);

        RestAssuredMockMvc
                .given()
                .auth().none()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/authors/getUserByUsername/" + "Test User")
                .then()
                .statusCode(404);
    }

    @Test
    @WithMockUser(username = "ensar", roles = "AUTHOR")
    public void test_updateAuthor_shouldAllowAuthorUpdateWithAuthentication() throws Exception {
        UUID authorId = UUID.randomUUID();

        String imagePath = new ClassPathResource("wall1677252684787.jpg").getPath();
        Path path = Paths.get(System.getProperty("user.dir") + "/src/test/resources/" + imagePath);
        MultipartFile multipartFile = new MockMultipartFile("wall1677252684787.jpg",
                "wall1677252684787.jpg", "image/jpeg", Files.readAllBytes(path));

        // when
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders
                .multipart(HttpMethod.PUT,"/api/authors/updateAuthor/" + authorId)
                .file("image", multipartFile.getBytes())
                .param("firstName", "testFirstName")
                .param("lastName", "testLastName")
                .param("description", "AnyWay")
                .param("imageProtect", "false"));

        // then
        ArgumentCaptor<AuthorUpdateRequestDTO> captor = ArgumentCaptor.forClass(AuthorUpdateRequestDTO.class);
        verify(authorService, times(1)).updateUser(eq(authorId.toString()),captor.capture());
        assertEquals(captor.getValue().getFirstName(), "testFirstName");
        actions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "ensar", roles = "USER")
    public void test_updateAuthor_shouldReturn403AuthorizationError() throws Exception {
        UUID authorId = UUID.randomUUID();

        String imagePath = new ClassPathResource("wall1677252684787.jpg").getPath();
        Path path = Paths.get(System.getProperty("user.dir") + "/src/test/resources/" + imagePath);
        MultipartFile multipartFile = new MockMultipartFile("wall1677252684787.jpg",
                "wall1677252684787.jpg", "image/jpeg", Files.readAllBytes(path));

        // when
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders
                .multipart(HttpMethod.PUT,"/api/authors/updateAuthor/" + authorId)
                .file("image", multipartFile.getBytes())
                .param("firstName", "testFirstName")
                .param("lastName", "testLastName")
                .param("description", "AnyWay")
                .param("imageProtect", "false"));

        // then
        actions.andExpect(status().isForbidden());
    }

    @Test
    public void test_updateAuthor_shouldReturn401AuthenticationError() throws Exception {
        UUID authorId = UUID.randomUUID();

        String imagePath = new ClassPathResource("wall1677252684787.jpg").getPath();
        Path path = Paths.get(System.getProperty("user.dir") + "/src/test/resources/" + imagePath);
        MultipartFile multipartFile = new MockMultipartFile("wall1677252684787.jpg",
                "wall1677252684787.jpg", "image/jpeg", Files.readAllBytes(path));

        // when
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders
                .multipart(HttpMethod.PUT,"/api/authors/updateAuthor/" + authorId)
                .file("image", multipartFile.getBytes())
                .param("firstName", "testFirstName")
                .param("lastName", "testLastName")
                .param("description", "AnyWay")
                .param("imageProtect", "false"));

        // then
        actions.andExpect(status().isUnauthorized());
    }

    @Test
    public void RA_test_makeAuthor_shouldAllowMakeAuthor() {
        UUID authorId = UUID.randomUUID();
        AuthorResponseDTO responseDTO = AuthorResponseDTO.builder()
                .id(authorId)
                .username("Test User")
                .build();

        Mockito.when(authorService.makeAuthor(authorId.toString())).thenReturn(responseDTO);

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/api/authors/makeAuthor/" + authorId)
                .then()
                .statusCode(200)
                .body("username", Matchers.equalTo("Test User"));
    }

    @Test
    public void RA_test_makeAuthor_shouldReturn403AuthorizationError() {
        UUID authorId = UUID.randomUUID();
        AuthorResponseDTO responseDTO = AuthorResponseDTO.builder()
                .id(authorId)
                .username("Test User")
                .build();

        Mockito.when(authorService.makeAuthor(authorId.toString())).thenReturn(responseDTO);

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("EDITOR"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/api/authors/makeAuthor/" + authorId)
                .then()
                .statusCode(403);
    }

    @Test
    public void RA_test_makeAuthor_shouldReturn401AuthenticationError() {
        UUID authorId = UUID.randomUUID();
        AuthorResponseDTO responseDTO = AuthorResponseDTO.builder()
                .id(authorId)
                .username("Test User")
                .build();

        Mockito.when(authorService.makeAuthor(authorId.toString())).thenReturn(responseDTO);

        RestAssuredMockMvc
                .given()
                .auth().none()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/api/authors/makeAuthor/" + authorId)
                .then()
                .statusCode(401);
    }

    @Test
    public void RA_test_makeEditor_shouldAllowMakeEditor() {
        UUID authorId = UUID.randomUUID();
        AuthorResponseDTO responseDTO = AuthorResponseDTO.builder()
                .id(authorId)
                .username("Test User")
                .build();

        Mockito.when(authorService.makeEditor(authorId.toString())).thenReturn(responseDTO);

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/api/authors/makeEditor/" + authorId)
                .then()
                .statusCode(200)
                .body("username", Matchers.equalTo("Test User"));
    }

    @Test
    public void RA_test_makeEditor_shouldReturn403AuthorizationError() {
        UUID authorId = UUID.randomUUID();
        AuthorResponseDTO responseDTO = AuthorResponseDTO.builder()
                .id(authorId)
                .username("Test User")
                .build();

        Mockito.when(authorService.makeEditor(authorId.toString())).thenReturn(responseDTO);

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("EDITOR"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/api/authors/makeEditor/" + authorId)
                .then()
                .statusCode(403);
    }

    @Test
    public void RA_test_makeEditor_shouldReturn401AuthenticationError() {
        UUID authorId = UUID.randomUUID();
        AuthorResponseDTO responseDTO = AuthorResponseDTO.builder()
                .id(authorId)
                .username("Test User")
                .build();

        Mockito.when(authorService.makeEditor(authorId.toString())).thenReturn(responseDTO);

        RestAssuredMockMvc
                .given()
                .auth().none()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/api/authors/makeEditor/" + authorId)
                .then()
                .statusCode(401);
    }

    @Test
    public void RA_test_deactivateUser_shouldReturn200() throws AccessDeniedException {
        UUID authorId = UUID.randomUUID();
        AuthorResponseDTO responseDTO = AuthorResponseDTO.builder()
                .id(authorId)
                .username("Test User")
                .build();

        Mockito.when(authorService.deactivateUser(authorId.toString())).thenReturn(responseDTO);

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("AUTHOR"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/api/authors/deactivateAuthor/" + authorId)
                .then()
                .statusCode(200)
                .body("username", Matchers.equalTo("Test User"));
    }

    @Test
    public void RA_test_deactivateUser_shouldReturn401AuthenticationError() throws AccessDeniedException {
        UUID authorId = UUID.randomUUID();
        AuthorResponseDTO responseDTO = AuthorResponseDTO.builder()
                .id(authorId)
                .username("Test User")
                .build();

        Mockito.when(authorService.deactivateUser(authorId.toString())).thenReturn(responseDTO);

        RestAssuredMockMvc
                .given()
                .auth().none()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/api/authors/deactivateAuthor/" + authorId)
                .then()
                .statusCode(401);
    }

    @Test
    public void RA_test_deleteUser_shouldAllowDeleteUser() {
        UUID authorId = UUID.randomUUID();
        MessageResponseDTO responseDTO =
                new MessageResponseDTO("Author named " + authorId + " is deleted successfully.");

        Mockito.when(authorService.deleteCompleteUser("Test User")).thenReturn(responseDTO);

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/api/authors/removeUser/" + "Test User")
                .then()
                .statusCode(200)
                .body("message",Matchers.equalTo(responseDTO.getMessage()));
    }

    @Test
    public void RA_test_deleteUser_shouldReturn403AuthorizationError() {

        RestAssuredMockMvc
                .given()
                .auth().with(SecurityMockMvcRequestPostProcessors.user("ensar").roles("EDITOR"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/api/authors/removeUser/" + "Test User")
                .then()
                .statusCode(403);
    }

    @Test
    public void RA_test_deleteUser_shouldReturn401AuthenticationError() {

        RestAssuredMockMvc
                .given()
                .auth().none()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/api/authors/removeUser/" + "Test User")
                .then()
                .statusCode(401);
    }
}