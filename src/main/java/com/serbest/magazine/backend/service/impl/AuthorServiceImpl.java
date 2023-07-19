package com.serbest.magazine.backend.service.impl;

import com.serbest.magazine.backend.dto.author.AuthorCardResponseDTO;
import com.serbest.magazine.backend.dto.author.AuthorListResponseDTO;
import com.serbest.magazine.backend.dto.author.UserListResponseDTO;
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
import com.serbest.magazine.backend.service.AuthorService;
import com.serbest.magazine.backend.service.ImageModelService;
import com.serbest.magazine.backend.util.UploadImage;
import io.jsonwebtoken.lang.Assert;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final RoleRepository roleRepository;
    private final AuthorRepository authorRepository;
    private final UserMapper userMapper;
    private final CheckAuthorization checkAuthorization;
    private final ImageModelService imageModelService;

    public AuthorServiceImpl(RoleRepository roleRepository, AuthorRepository authorRepository, UserMapper userMapper,
                             CheckAuthorization checkAuthorization, ImageModelService imageModelService) {
        this.roleRepository = roleRepository;
        this.authorRepository = authorRepository;
        this.userMapper = userMapper;
        this.checkAuthorization = checkAuthorization;
        this.imageModelService = imageModelService;
    }

    @Override
    public List<UserListResponseDTO> getAllUsers() {
        List<Author> users = authorRepository.findAll();

        return users.stream().map(userMapper::authorToUserListResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<AuthorResponseDTO> getUsers() {
        List<Role> roles = new ArrayList<>();
        Optional<Role> role = roleRepository.findByName("ROLE_USER");
        roles.add(role.get());
        List<Author> authors = authorRepository.findByRolesIn(roles);
        return authors.stream().map(
                userMapper::authorToAuthorResponseDTO
        ).collect(Collectors.toList());
    }

    @Override
    public List<AuthorListResponseDTO> getAuthors() {
        List<Role> roles = new ArrayList<>();
        Role role = roleRepository.findByName("ROLE_AUTHOR").orElseThrow(
                () -> new ResourceNotFoundException("Role","RoleName","ROLE_AUTHOR")
        );
        roles.add(role);
        List<Author> authors = authorRepository.findByRolesIn(roles);
        return authors.stream().map(
                author -> new AuthorListResponseDTO(author.getId(), author.getUsername())
        ).collect(Collectors.toList());
    }

    @Override
    public List<AuthorCardResponseDTO> getAuthorsForCard() {
        List<Role> roles = new ArrayList<>();
        Optional<Role> role = roleRepository.findByName("ROLE_AUTHOR");
        roles.add(role.get());
        List<Author> authors = authorRepository.findByRolesIn(roles);
        return authors.stream().map(userMapper::authorToAuthorCardResponseDTO).collect(Collectors.toList());
    }

    @Override
    public AuthorResponseDTO updateUser(String userId, AuthorUpdateRequestDTO requestDTO) throws IOException {
        validateAndSanitizeFieldName("Author Id", userId);
        Author author = getAuthor(userId);


        author.setFirstName(requestDTO.getFirstName());
        author.setLastName(requestDTO.getLastName());
        author.setDescription(requestDTO.getDescription());
        author.setFacebook(requestDTO.getFacebook());
        author.setInstagram(requestDTO.getInstagram());
        author.setTwitter(requestDTO.getTwitter());
        author.setYoutube(requestDTO.getYoutube());
        author.setBlog(requestDTO.getBlog());
        if (!requestDTO.getImageProtect()) {
            author.setProfileImage(UploadImage.uploadImage(requestDTO.getImage()));
        }

        try {
            return userMapper.authorToAuthorResponseDTO(authorRepository.save(author));
        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public AuthorResponseDTO makeAuthor(String userId) {
        validateAndSanitizeFieldName("Author Id", userId);

        Author author = authorRepository.findById(UUID.fromString(userId)).orElseThrow(
                () -> new ResourceNotFoundException("Author", "id", userId)
        );

        Role authorRole = roleRepository.findByName("ROLE_AUTHOR").orElseThrow(
                () -> new ResourceNotFoundException("Role", "name", "ROLE_AUTHOR")
        );

        try {
            Set<Role> userRoles = author.getRoles();

            if (!userRoles.contains(authorRole)) {
                userRoles.add(authorRole);
            }
            author.setRoles(userRoles);

            return userMapper.authorToAuthorResponseDTO(authorRepository.save(author));
        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public AuthorResponseDTO unMakeAuthor(String userId) {
        validateAndSanitizeFieldName("Author Id", userId);

        Author author = authorRepository.findById(UUID.fromString(userId)).orElseThrow(
                () -> new ResourceNotFoundException("Author", "id", userId)
        );

        Role authorRole = roleRepository.findByName("ROLE_AUTHOR").orElseThrow(
                () -> new ResourceNotFoundException("Role", "name", "ROLE_AUTHOR")
        );

        try {
            author.getRoles().remove(authorRole);
            return userMapper.authorToAuthorResponseDTO(authorRepository.save(author));
        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    @Override
    public AuthorResponseDTO makeEditor(String userId) {
        validateAndSanitizeFieldName("Author Id", userId);

        Author author = authorRepository.findById(UUID.fromString(userId)).orElseThrow(
                () -> new ResourceNotFoundException("Author", "id", userId)
        );

        Role editorRole = roleRepository.findByName("ROLE_EDITOR").orElseThrow(
                () -> new ResourceNotFoundException("Role", "name", "ROLE_EDITOR")
        );

        try {
            Set<Role> userRoles = author.getRoles();

            if (!userRoles.contains(editorRole)) {
                userRoles.add(editorRole);
            }
            author.setRoles(userRoles);

            return userMapper.authorToAuthorResponseDTO(authorRepository.save(author));
        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public AuthorResponseDTO unMakeEditor(String userId) {
        validateAndSanitizeFieldName("Author Id", userId);

        Author author = authorRepository.findById(UUID.fromString(userId)).orElseThrow(
                () -> new ResourceNotFoundException("Author", "id", userId)
        );

        Role editorRole = roleRepository.findByName("ROLE_EDITOR").orElseThrow(
                () -> new ResourceNotFoundException("Role", "name", "ROLE_EDITOR")
        );

        try {
            author.getRoles().remove(editorRole);
            return userMapper.authorToAuthorResponseDTO(authorRepository.save(author));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    @Override
    public AuthorResponseDTO getAuthorByUsername(String username) {
        validateAndSanitizeFieldName("Username", username);

        Author author = authorRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("Author", "username", username)
        );

        return userMapper.authorToAuthorResponseDTO(author);
    }

    @Override
    public AuthorResponseDTO deactivateUser(String userId) throws AccessDeniedException {
        validateAndSanitizeFieldName("Author Id", userId);

        Author author = getAuthor(userId);
        author.setActive(false);
        return userMapper.authorToAuthorResponseDTO(authorRepository.save(author));
    }

    @Override
    public MessageResponseDTO deleteCompleteUser(String username) {
        validateAndSanitizeFieldName("Username", username);

        Author author = authorRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("Author", "username", username)
        );
        author.setRoles(new HashSet<>());
        try {
            authorRepository.delete(author);
            return new MessageResponseDTO("Author with id " + author.getId() + " is deleted successfully.");
        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private Author getAuthor(String userId) throws AccessDeniedException {

        Author author = authorRepository.findById(UUID.fromString(userId)).orElseThrow(
                () -> new ResourceNotFoundException("Author", "id", userId)
        );
        checkAuthorization.checkUser(author);
        return author;
    }

    private void validateAndSanitizeFieldName(String fieldName, String fieldValue) {
        Assert.notNull(fieldValue, "Provide a valid " + fieldName + " , please.");
        if (fieldValue.isEmpty() || fieldValue.isBlank()) {
            throw new IllegalArgumentException("Provide a valid " + fieldName + " , please.");
        }
    }
}
