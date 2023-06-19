package com.serbest.magazine.backend.service.impl;

import com.serbest.magazine.backend.dto.author.AuthorCardResponseDTO;
import com.serbest.magazine.backend.dto.author.AuthorListResponseDTO;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.dto.user.AuthorResponseDTO;
import com.serbest.magazine.backend.dto.user.AuthorUpdateRequestDTO;
import com.serbest.magazine.backend.entity.Author;
import com.serbest.magazine.backend.entity.Role;
import com.serbest.magazine.backend.exception.CustomApplicationException;
import com.serbest.magazine.backend.exception.ResourceNotFoundException;
import com.serbest.magazine.backend.mapper.UserMapper;
import com.serbest.magazine.backend.repository.AuthorRepository;
import com.serbest.magazine.backend.repository.RoleRepository;
import com.serbest.magazine.backend.service.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class AuthorServiceImplIntegrationTest {

    @Autowired
    AuthorService authorService;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RoleRepository roleRepository;

    UUID authorId;

    @BeforeEach
    void createRoles() {

        Authentication authentication = Mockito.mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("testUser");
        SecurityContextHolder.setContext(securityContext);

        roleRepository.save(new Role("ROLE_ADMIN"));
        roleRepository.save(new Role("ROLE_EDITOR"));
        roleRepository.save(new Role("ROLE_AUTHOR"));
        roleRepository.save(new Role("ROLE_USER"));

        Author testUser = Author.Builder.newBuilder()
                .username("testUser")
                .password("testpassword")
                .email("test@email.com")
                .active(true)
                .createDateTime(LocalDateTime.now())
                .build();

        Author author = authorRepository.save(testUser);

        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").get();
        Role roleEditor = roleRepository.findByName("ROLE_EDITOR").get();
        Role roleAuthor = roleRepository.findByName("ROLE_AUTHOR").get();
        Role roleUser = roleRepository.findByName("ROLE_USER").get();

        HashSet hashSet = new HashSet<>();
        hashSet.add(roleAdmin);
        hashSet.add(roleEditor);
        hashSet.add(roleAuthor);
        hashSet.add(roleUser);

        author.setRoles(hashSet);

        Author authorWithRoles = authorRepository.save(author);
        this.authorId = authorWithRoles.getId();

    }

    @Test
    public void testIntegration_getUsers_withSuccess() {

        List<AuthorResponseDTO> responseDTOS = authorService.getUsers();

        assertEquals(1, responseDTOS.size());
        assertEquals("testUser", responseDTOS.get(0).getUsername());
    }

    @Test
    public void testIntegration_getAuthors_withSuccess() {

        List<AuthorListResponseDTO> responseDTOS = authorService.getAuthors();

        assertEquals(1, responseDTOS.size());
        assertEquals("testUser", responseDTOS.get(0).getUsername());
    }

    @Test
    public void testIntegration_getAuthorsForCard_withSuccess() {

        List<AuthorCardResponseDTO> responseDTOS = authorService.getAuthorsForCard();

        assertEquals(1, responseDTOS.size());
        assertEquals("testUser", responseDTOS.get(0).getUsername());
    }

    @Test
    public void testIntegration_updateUser_withSuccess() throws IOException {
        AuthorUpdateRequestDTO requestDTO = AuthorUpdateRequestDTO.builder()
                .imageProtect(true)
                .description("Test Description")
                .build();

        AuthorResponseDTO responseDTO = authorService.updateUser(this.authorId.toString(), requestDTO);

        assertEquals("Test Description", responseDTO.getDescription());

    }

    @Test
    public void testIntegration_updateUser_authorIdNotProvided() {
        assertThrows(
                IllegalArgumentException.class,
                () -> authorService.updateUser("", new AuthorUpdateRequestDTO())
        );

    }

    @Test
    public void testIntegration_updateUser_authorNotFound() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.updateUser(UUID.randomUUID().toString(), new AuthorUpdateRequestDTO())
        );

    }

    @Test
    public void testIntegration_makeAuthor_withSuccess() {
        Author author = authorRepository.save(Author.Builder.newBuilder()
                .id(UUID.randomUUID())
                .username("testUser2")
                .password("testpassword")
                .email("test2@email.com")
                .active(true)
                .createDateTime(LocalDateTime.now())
                .build());

        AuthorResponseDTO responseDTO = authorService.makeAuthor(author.getId().toString());

        Author author1 = authorRepository.findByUsername("testUser2").get();
        List<Role> roles = author1.getRoles().stream().toList();


        assertEquals("testUser2", responseDTO.getUsername());
        assertTrue(roles.stream().anyMatch(role -> "ROLE_AUTHOR".equals(role.getName())));
    }

    @Test
    public void testIntegration_makeAuthor_authorIdNotProvided() {
        assertThrows(
                IllegalArgumentException.class,
                () -> authorService.makeAuthor("")
        );

    }

    @Test
    public void testIntegration_makeAuthor_authorNotFound() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.makeAuthor(UUID.randomUUID().toString())
        );

    }

    @Test
    public void testIntegration_unMakeAuthor_withSuccess() {

        AuthorResponseDTO responseDTO = authorService.unMakeAuthor(this.authorId.toString());

        assertEquals("testUser", responseDTO.getUsername());

    }

    @Test
    public void testIntegration_unMakeAuthor_authorIdNotProvided() {
        assertThrows(
                IllegalArgumentException.class,
                () -> authorService.unMakeAuthor("")
        );

    }

    @Test
    public void testIntegration_unMakeAuthor_authorNotFound() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.unMakeAuthor(UUID.randomUUID().toString())
        );

    }

    @Test
    public void testIntegration_makeEditor_withSuccess() {
        Author author = authorRepository.save(Author.Builder.newBuilder()
                .id(UUID.randomUUID())
                .username("testUser2")
                .password("testpassword")
                .email("test2@email.com")
                .active(true)
                .createDateTime(LocalDateTime.now())
                .build());

        AuthorResponseDTO responseDTO = authorService.makeEditor(author.getId().toString());

        assertEquals("testUser2", responseDTO.getUsername());

    }

    @Test
    public void testIntegration_makeEditor_authorIdNotProvided() {
        assertThrows(
                IllegalArgumentException.class,
                () -> authorService.makeEditor("")
        );

    }

    @Test
    public void testIntegration_makeEditor_authorNotFound() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.makeEditor(UUID.randomUUID().toString())
        );

    }

    @Test
    public void testIntegration_unMakeEditor_withSuccess() {

        AuthorResponseDTO responseDTO = authorService.unMakeEditor(this.authorId.toString());
        assertEquals("testUser", responseDTO.getUsername());

    }

    @Test
    public void testIntegration_unMakeEditor_authorIdNotProvided() {
        assertThrows(
                IllegalArgumentException.class,
                () -> authorService.unMakeEditor("")
        );

    }

    @Test
    public void testIntegration_unMakeEditor_authorNotFound() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.unMakeEditor(UUID.randomUUID().toString())
        );

    }

    @Test
    public void testIntegration_getAuthorByUsername_withSuccess() {

        AuthorResponseDTO responseDTO = authorService.getAuthorByUsername("testUser");

        assertEquals("testUser", responseDTO.getUsername());
    }

    @Test
    public void testIntegration_getAuthorByUsername_notAuthor() {

        Author author = authorRepository.save(Author.Builder.newBuilder()
                .id(UUID.randomUUID())
                .username("testUser2")
                .password("testpassword")
                .email("test2@email.com")
                .active(true)
                .createDateTime(LocalDateTime.now())
                .build());

        authorRepository.save(author);

        assertThrows(
                CustomApplicationException.class,
                () -> authorService.getAuthorByUsername("testUser2")
        );
    }

    @Test
    public void testIntegration_getAuthorByUsername_authorIdNotProvided() {
        assertThrows(
                IllegalArgumentException.class,
                () -> authorService.getAuthorByUsername("")
        );

    }

    @Test
    public void testIntegration_getAuthorByUsername_authorNotFound() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.getAuthorByUsername("wrongUsername")
        );

    }

    @Test
    public void testIntegration_deactivateUser_withSuccess() throws AccessDeniedException {

        AuthorResponseDTO responseDTO = authorService.deactivateUser(this.authorId.toString());

        assertEquals("testUser", responseDTO.getUsername());

    }

    @Test
    public void testIntegration_deactivateUser_authorIdNotProvided() {
        assertThrows(
                IllegalArgumentException.class,
                () -> authorService.deactivateUser("")
        );

    }

    @Test
    public void testIntegration_deactivateUser_authorNotFound() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.deactivateUser(UUID.randomUUID().toString())
        );

    }

    @Test
    public void testIntegration_deleteCompleteUser_withSuccess() throws AccessDeniedException {

        MessageResponseDTO responseDTO = authorService.deleteCompleteUser("testUser");

        assertEquals(
                "Author with id " + authorId + " is deleted successfully."
                , responseDTO.getMessage());

    }

    @Test
    public void testIntegration_deleteCompleteUser_authorIdNotProvided() {
        assertThrows(
                IllegalArgumentException.class,
                () -> authorService.deleteCompleteUser("")
        );

    }

    @Test
    public void testIntegration_deleteCompleteUser_authorNotFound() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.deleteCompleteUser(UUID.randomUUID().toString())
        );

    }

}