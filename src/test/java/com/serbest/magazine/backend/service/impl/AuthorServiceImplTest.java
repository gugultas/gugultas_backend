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
import com.serbest.magazine.backend.security.CheckAuthorization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @InjectMocks
    AuthorServiceImpl authorService;

    @Mock
    RoleRepository roleRepository;

    @Mock
    AuthorRepository authorRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    CheckAuthorization checkAuthorization;

    @Mock
    ImageModelServiceImpl imageModelService;

    @Test
    public void test_getUsers_withSuccess() {
        Author author = Author.Builder.newBuilder()
                .username("Test User")
                .email("test@email.com")
                .build();

        Role mockRole = mock(Role.class);

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(mockRole));
        when(authorRepository.findByRolesIn(List.of(mockRole))).thenReturn(List.of(author));

        List<AuthorResponseDTO> responseDTOS = authorService.getUsers();

        assertEquals(1, responseDTOS.size());
    }

    @Test
    public void test_getAuthors_withSuccess() {
        Author author = Author.Builder.newBuilder()
                .username("Test User")
                .email("test@email.com")
                .build();

        Role mockRole = mock(Role.class);

        when(roleRepository.findByName("ROLE_AUTHOR")).thenReturn(Optional.of(mockRole));
        when(authorRepository.findByRolesIn(List.of(mockRole))).thenReturn(List.of(author));

        List<AuthorListResponseDTO> responseDTOS = authorService.getAuthors();

        assertEquals(1, responseDTOS.size());
    }

    @Test
    public void test_getAuthorsForCard_withSuccess() {
        Author author = Author.Builder.newBuilder()
                .username("Test User")
                .email("test@email.com")
                .build();

        Role mockRole = mock(Role.class);

        when(roleRepository.findByName("ROLE_AUTHOR")).thenReturn(Optional.of(mockRole));
        when(authorRepository.findByRolesIn(List.of(mockRole))).thenReturn(List.of(author));
        when(userMapper.authorToAuthorCardResponseDTO(author)).thenReturn(
                AuthorCardResponseDTO.builder()
                        .username("Test User")
                        .build()
        );

        List<AuthorCardResponseDTO> responseDTOS = authorService.getAuthorsForCard();

        assertEquals(1, responseDTOS.size());
        assertEquals("Test User", responseDTOS.get(0).getUsername());
    }

    @Test
    public void test_updateUser_withSuccess() throws IOException {
        UUID authorId = UUID.randomUUID();

        Author author = Author.Builder.newBuilder().username("Test User").build();

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(authorRepository.save(author)).thenReturn(author);
        when(userMapper.authorToAuthorResponseDTO(author)).thenReturn(
                AuthorResponseDTO.builder().description("Test Description").build()
        );

        AuthorResponseDTO responseDTO = authorService.updateUser(authorId.toString(),
                AuthorUpdateRequestDTO.builder()
                        .description("Test Description")
                        .imageProtect(true)
                        .build());

        assertEquals("Test Description", responseDTO.getDescription());

    }

    @Test
    public void test_updateUser_authorIdNotProvided() {
        assertThrows(
                IllegalArgumentException.class,
                () -> authorService.updateUser("", new AuthorUpdateRequestDTO())
        );
    }

    @Test
    public void test_updateUser_authorNotFound() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.updateUser(UUID.randomUUID().toString(), new AuthorUpdateRequestDTO())
        );
    }

    @Test
    public void test_makeAuthor_withSuccess() {
        UUID authorId = UUID.randomUUID();

        Role roleMock = new Role("ROLE_AUTHOR");
        Author author = Author.Builder.newBuilder().username("testUser")
                .roles(Set.of(new Role("ROLE_USER")))
                .build();

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(roleRepository.findByName("ROLE_AUTHOR")).thenReturn(Optional.of(roleMock));

        author.setRoles(Set.of(roleMock));
        when(authorRepository.save(author)).thenReturn(author);
        when(userMapper.authorToAuthorResponseDTO(author)).thenReturn(AuthorResponseDTO.builder()
                .username("testUser")
                .build());

        AuthorResponseDTO responseDTO = authorService.makeAuthor(authorId.toString());

        assertEquals("testUser", responseDTO.getUsername());

    }

    @Test
    public void test_makeAuthor_authorNotFound(){
        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.makeAuthor(UUID.randomUUID().toString())
        );
    }

    @Test
    public void test_makeAuthor_roleNotFound(){
        UUID authorId = UUID.randomUUID();
        Author mockAuthor = mock(Author.class);

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(mockAuthor));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.makeAuthor(authorId.toString())
        );

        assertEquals("Role not found with name : 'ROLE_AUTHOR'",exception.getMessage());
    }

    @Test
    public void test_unMakeAuthor_withSuccess() {
        UUID authorId = UUID.randomUUID();

        Role roleMock = new Role("ROLE_AUTHOR");
        HashSet<Role> userRoles = new HashSet<>();
        userRoles.add(roleMock);
        Author author = Author.Builder.newBuilder()
                .id(authorId)
                .username("testUser")
                .roles(userRoles)
                .build();

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(roleRepository.findByName("ROLE_AUTHOR")).thenReturn(Optional.of(roleMock));

        when(authorRepository.save(any(Author.class))).thenReturn(author);
        when(userMapper.authorToAuthorResponseDTO(author)).thenReturn(AuthorResponseDTO.builder()
                .username("testUser")
                .build());

        AuthorResponseDTO responseDTO = authorService.unMakeAuthor(authorId.toString());

        assertEquals("testUser", responseDTO.getUsername());

    }

    @Test
    public void test_unMakeAuthor_authorNotFound(){
        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.unMakeAuthor(UUID.randomUUID().toString())
        );
    }

    @Test
    public void test_unMakeAuthor_roleNotFound(){
        UUID authorId = UUID.randomUUID();
        Author mockAuthor = mock(Author.class);

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(mockAuthor));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.unMakeAuthor(authorId.toString())
        );

        assertEquals("Role not found with name : 'ROLE_AUTHOR'",exception.getMessage());
    }

    @Test
    public void test_makeEditor_withSuccess() {
        UUID authorId = UUID.randomUUID();

        Role roleMock = mock(Role.class);
        Author author = Author.Builder.newBuilder().username("testUser")
                .roles(Set.of(new Role("ROLE_USER")))
                .build();

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(roleRepository.findByName("ROLE_EDITOR")).thenReturn(Optional.of(roleMock));

        author.setRoles(Set.of(roleMock));
        when(authorRepository.save(author)).thenReturn(author);
        when(userMapper.authorToAuthorResponseDTO(author)).thenReturn(AuthorResponseDTO.builder()
                .username("testUser")
                .build());

        AuthorResponseDTO responseDTO = authorService.makeEditor(authorId.toString());

        assertEquals("testUser", responseDTO.getUsername());

    }

    @Test
    public void test_makeEditor_authorNotFound(){
        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.makeEditor(UUID.randomUUID().toString())
        );
    }

    @Test
    public void test_makeEditor_roleNotFound(){
        UUID authorId = UUID.randomUUID();
        Author mockAuthor = mock(Author.class);

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(mockAuthor));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.makeEditor(authorId.toString())
        );

        assertEquals("Role not found with name : 'ROLE_EDITOR'",exception.getMessage());
    }

    @Test
    public void test_getAuthorByUsername_withSuccess() {
        Author author = Author.Builder.newBuilder()
                .username("Test User")
                .email("test@email.com")
                .build();

        Role mockRole = mock(Role.class);

        when(mockRole.getName()).thenReturn("ROLE_AUTHOR");
        author.setRoles(Set.of(mockRole));
        when(authorRepository.findByUsername("Test User")).thenReturn(Optional.of(author));

        when(roleRepository.findByName("ROLE_AUTHOR")).thenReturn(Optional.of(mockRole));
        when(userMapper.authorToAuthorResponseDTO(author)).thenReturn(AuthorResponseDTO.builder().username("Test User")
                .build());


        AuthorResponseDTO responseDTO = authorService.getAuthorByUsername("Test User");

        assertEquals("Test User", responseDTO.getUsername());
    }

    @Test
    public void test_getAuthorByUsername_usernameNotProvided(){
        assertThrows(
                IllegalArgumentException.class,
                () -> authorService.getAuthorByUsername("")
        );
    }

    @Test
    public void test_getAuthorByUsername_authorNotFound(){
        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.getAuthorByUsername("Wrong User")
        );
    }

    @Test
    public void test_getAuthorByUsername_roleNotFound(){
        UUID authorId = UUID.randomUUID();
        Author mockAuthor = mock(Author.class);

        when(authorRepository.findByUsername("testUser")).thenReturn(Optional.of(mockAuthor));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.getAuthorByUsername("testUser")
        );

        assertEquals("Role not found with name : 'ROLE_AUTHOR'",exception.getMessage());
    }

    @Test
    public void test_getAuthorByUsername_userWithoutRole() {
        Author author = Author.Builder.newBuilder()
                .username("Test User")
                .email("test@email.com")
                .build();

        Role mockRole = mock(Role.class);

        when(authorRepository.findByUsername("Test User")).thenReturn(Optional.of(author));

        when(roleRepository.findByName("ROLE_AUTHOR")).thenReturn(Optional.of(mockRole));

        assertThrows(
                CustomApplicationException.class,
                () -> authorService.getAuthorByUsername("Test User")
        );
    }

    @Test
    public void test_deactivateUser_withSuccess() throws AccessDeniedException {
        UUID authorId = UUID.randomUUID();

        Author author = Author.Builder.newBuilder().username("testUser").build();

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

        when(authorRepository.save(author)).thenReturn(author);

        when(userMapper.authorToAuthorResponseDTO(author)).thenReturn(AuthorResponseDTO.builder()
                .username("testUser")
                .build());

        AuthorResponseDTO responseDTO = authorService.deactivateUser(authorId.toString());

        assertEquals("testUser", responseDTO.getUsername());

    }

    @Test
    public void test_deactivateUser_authorIdNotProvided(){
        assertThrows(
                IllegalArgumentException.class,
                () -> authorService.deactivateUser("")
        );
    }

    @Test
    public void test_deactivateUser_authorNotFound(){
        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.deactivateUser(UUID.randomUUID().toString())
        );
    }

    @Test
    public void test_deleteCompleteUser_withSuccess() {
        UUID authorId = UUID.randomUUID();
        Author author = Author.Builder.newBuilder()
                .id(authorId)
                .username("Test User")
                .email("test@email.com")
                .build();

        when(authorRepository.findByUsername("Test User")).thenReturn(Optional.of(author));

        MessageResponseDTO responseDTO = authorService.deleteCompleteUser("Test User");

        assertEquals(
                "Author with id " + authorId + " is deleted successfully."
                , responseDTO.getMessage());
    }

    @Test
    public void test_deleteCompleteUser_usernameNotProvided(){
        assertThrows(
                IllegalArgumentException.class,
                () -> authorService.deleteCompleteUser("")
        );
    }

    @Test
    public void test_deleteCompleteUser_authorNotFound(){
        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.deleteCompleteUser("Wrong User")
        );
    }



}