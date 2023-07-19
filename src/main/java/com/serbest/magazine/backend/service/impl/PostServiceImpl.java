package com.serbest.magazine.backend.service.impl;

import com.serbest.magazine.backend.service.ImageModelService;
import com.serbest.magazine.backend.service.PostService;
import com.serbest.magazine.backend.dto.post.*;
import com.serbest.magazine.backend.entity.Author;
import com.serbest.magazine.backend.entity.Category;
import com.serbest.magazine.backend.entity.Post;
import com.serbest.magazine.backend.entity.SubCategory;
import com.serbest.magazine.backend.exception.CustomApplicationException;
import com.serbest.magazine.backend.exception.ResourceNotFoundException;
import com.serbest.magazine.backend.mapper.PostMapper;
import com.serbest.magazine.backend.repository.AuthorRepository;
import com.serbest.magazine.backend.repository.CategoryRepository;
import com.serbest.magazine.backend.repository.PostRepository;
import com.serbest.magazine.backend.repository.SubCategoryRepository;
import com.serbest.magazine.backend.security.CheckAuthorization;
import com.serbest.magazine.backend.util.UploadImage;

import io.jsonwebtoken.lang.Assert;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final CheckAuthorization checkAuthorization;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final AuthorRepository userRepository;
    private final ImageModelService imageModelService;

    public PostServiceImpl(CheckAuthorization checkAuthorization, CategoryRepository categoryRepository,
                           SubCategoryRepository subCategoryRepository, PostRepository postRepository, PostMapper postMapper, AuthorRepository userRepository, ImageModelService imageModelService) {
        this.checkAuthorization = checkAuthorization;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.userRepository = userRepository;
        this.imageModelService = imageModelService;
    }

    @Override
    public PostCreateResponseDTO createPost(PostRequestDTO requestDTO) {
        validateAndSanitizeFieldName("Title", requestDTO.getTitle());
        validateAndSanitizeFieldName("Content", requestDTO.getContent());
        validateAndSanitizeFieldName("Category", requestDTO.getCategory());
        validateAndSanitizeFieldName("SubCategory", requestDTO.getSubCategory());

        SecurityContext context = SecurityContextHolder.getContext();
        String usernameOrEmail = context.getAuthentication().getName();

        Author user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(
                () -> new ResourceNotFoundException("Author", "emailOrUsername", usernameOrEmail)
        );

        Category category = categoryRepository.findByName(requestDTO.getCategory()).orElseThrow(
                () -> new ResourceNotFoundException("Category", "name", requestDTO.getCategory())
        );

        SubCategory subCategory = subCategoryRepository.findByName(requestDTO.getSubCategory()).orElseThrow(
                () -> new ResourceNotFoundException("Sub-Category", "name", requestDTO.getSubCategory())
        );

        if (!user.isEnabled()){
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST,
                    "Hesabınız aktive edilmemiştir.");
        }

        if (!category.getName().equals(subCategory.getCategory().getName())){
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST,
                    "The Sub-Category is not belong to selected category.");
        }

        Post post = null;

        try {
            post = postMapper.postRequestDTOToPost(requestDTO);
            post.setAuthor(user);
            post.setCategory(category);
            post.setSubCategory(subCategory);

            return postMapper.postToPostCreateResponseDTO(postRepository.save(post));
        } catch (IOException e) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public PostCreateResponseDTO createPostEditor(PostCreateEditorRequestDTO requestDTO) {
        validateAndSanitizeFieldName("Title", requestDTO.getTitle());
        validateAndSanitizeFieldName("Content", requestDTO.getContent());
        validateAndSanitizeFieldName("Category", requestDTO.getCategory());
        validateAndSanitizeFieldName("Sub-Category", requestDTO.getSubCategory());
        validateAndSanitizeFieldName("Author", requestDTO.getAuthor());

        Author user = userRepository.findByUsername(requestDTO.getAuthor()).orElseThrow(
                () -> new ResourceNotFoundException("Author", "username", requestDTO.getAuthor())
        );

        Category category = categoryRepository.findByName(requestDTO.getCategory()).orElseThrow(
                () -> new ResourceNotFoundException("Category", "name", requestDTO.getCategory())
        );

        SubCategory subCategory = subCategoryRepository.findByName(requestDTO.getSubCategory()).orElseThrow(
                () -> new ResourceNotFoundException("Sub-Category", "name", requestDTO.getSubCategory())
        );

        if (!user.isEnabled()){
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST,
                    "Hesabınız aktive edilmemiştir.");
        }

        if (!category.getName().equals(subCategory.getCategory().getName())){
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST,
                    "The Sub-Category is not belong to selected category.");
        }

        Post post = null;
        try {
            post = postMapper.postCreateEditorRequestDTOToPost(requestDTO);
            post.setAuthor(user);
            post.setCategory(category);
            post.setSubCategory(subCategory);

            return postMapper.postToPostCreateResponseDTO(postRepository.save(post));
        } catch (IOException e) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public List<PostResponseDTO> getAllPosts() {
        List<Post> posts = postRepository.findByActiveTrueOrderByCreateDateTimeDesc();

        return posts
                .stream()
                .map(postMapper::postToPostResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FirstFivePostsListDTO> getFirstFivePosts() {
        List<Post> posts = postRepository.findFirstFiveActiveTrueByCreateDateTime();

        return posts
                .stream()
                .map(postMapper::postToFirstFivePostsListDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MainPagePostsListDTO> getFourPostsForTop() {
        List<Post> posts = postRepository.findFourPostsActiveTrueByCreateDateTime();

        return posts
                .stream()
                .map(postMapper::postToMainPagePostsListDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MainPagePostsListDTO> getPostsForMainPage() {
        List<Post> posts = postRepository.findFifteenActiveTrueByCreateDateTimeOffset5();

        return posts
                .stream()
                .map(postMapper::postToMainPagePostsListDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeactivatedPostApiResponseDTO> getDeactivatedPost() {
        List<Post> posts = postRepository.findByActiveFalseOrderByCreateDateTimeDesc();

        return posts.stream().map(postMapper::postToDeactivatedPostApiResponseDTO).collect(Collectors.toList());
    }

    @Override
    public PostResponseDTO findById(String id) {
        Post post = postRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", id)
        );

        return postMapper.postToPostResponseDTO(post);
    }

    @Override
    public PostResponseDTO deactivatePost(String id) throws AccessDeniedException {
        Post post = getPost(id);
        post.setActive(false);

        try {
            return postMapper.postToPostResponseDTO(postRepository.save(post));
        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public PostResponseDTO activatePost(String id) {
        Post post = postRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", id)
        );
        post.setActive(true);

        try {
            return postMapper.postToPostResponseDTO(postRepository.save(post));
        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public PostResponseDTO updatePost(String id, PostUpdateRequestDTO requestDTO) throws IOException {
        validateAndSanitizeFieldName("Title", requestDTO.getTitle());
        validateAndSanitizeFieldName("Content", requestDTO.getContent());
        validateAndSanitizeFieldName("Category", requestDTO.getCategory());
        validateAndSanitizeFieldName("Sub-Category", requestDTO.getSubCategory());
        Post post = getPost(id);

        Category category = categoryRepository.findByName(requestDTO.getCategory()).orElseThrow(
                () -> new ResourceNotFoundException("Category", "name", requestDTO.getCategory())
        );

        SubCategory subCategory = subCategoryRepository.findByName(requestDTO.getSubCategory()).orElseThrow(
                () -> new ResourceNotFoundException("Sub-Category", "name", requestDTO.getSubCategory())
        );

        if (!category.getName().equals(subCategory.getCategory().getName())){
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST,
                    "The Sub-Category is not belong to selected category.");
        }

        try {
            if (!requestDTO.getImageProtect()) {
                post.setPostImage(UploadImage.uploadImage(requestDTO.getImage()));
            }
            post.setCategory(category);
            post.setSubCategory(subCategory);
            post.setTitle(requestDTO.getTitle());
            post.setSubtitle(requestDTO.getSubtitle());
            post.setContent(requestDTO.getContent());
            return postMapper.postToPostResponseDTO(postRepository.save(post));
        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public PostResponseDTO updatePostEditor(String id, PostUpdateEditorRequestDTO requestDTO) {
        validateAndSanitizeFieldName("Title", requestDTO.getTitle());
        validateAndSanitizeFieldName("Content", requestDTO.getContent());
        validateAndSanitizeFieldName("Category", requestDTO.getCategory());
        validateAndSanitizeFieldName("Sub-Category", requestDTO.getSubCategory());

        Post post = postRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", id)
        );

        Category category = categoryRepository.findByName(requestDTO.getCategory()).orElseThrow(
                () -> new ResourceNotFoundException("Category", "name", requestDTO.getCategory())
        );

        SubCategory subCategory = subCategoryRepository.findByName(requestDTO.getSubCategory()).orElseThrow(
                () -> new ResourceNotFoundException("Sub-Category", "name", requestDTO.getSubCategory())
        );

        if (!category.getName().equals(subCategory.getCategory().getName())){
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST,
                    "The Sub-Category is not belong to selected category.");
        }

        try {
            if (!requestDTO.getImageProtect()) {
                post.setPostImage(UploadImage.uploadImage(requestDTO.getImage()));
            }
            post.setCategory(category);
            post.setSubCategory(subCategory);
            post.setTitle(requestDTO.getTitle());
            post.setSubtitle(requestDTO.getSubtitle());
            post.setContent(requestDTO.getContent());
            return postMapper.postToPostResponseDTO(postRepository.save(post));
        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public List<PostResponseDTO> getRandomThreePost() {
        List<Post> posts = postRepository.findThreeActiveTrueByRandom();
        return posts
                .stream()
                .map(postMapper::postToPostResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponseDTO> getPostsByCategory(String categoryName) {
        categoryRepository.findByName(categoryName).orElseThrow(
                () -> new ResourceNotFoundException("Category", "name", categoryName)
        );
        List<Post> posts = postRepository.findAllByCategoryNameAndActiveTrueOrderByCreateDateTimeDesc(categoryName);
        return posts
                .stream()
                .map(postMapper::postToPostResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponseDTO> getPostsBySubCategory(String subCategoryId) {
        subCategoryRepository.findById(UUID.fromString(subCategoryId)).orElseThrow(
                () -> new ResourceNotFoundException("Sub Category", "id", subCategoryId)
        );
        List<Post> posts =
                postRepository
                        .findAllBySubCategoryIdAndActiveTrueOrderByCreateDateTimeDesc(UUID.fromString(subCategoryId));
        return posts
                .stream()
                .map(postMapper::postToPostResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponseDTO> findByUsername(String username) {
        userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("Author", "username", username)
        );
        List<Post> posts = postRepository.findAllByAuthorUsernameAndActiveTrueOrderByCreateDateTimeDesc(username);

        return posts
                .stream()
                .map(postMapper::postToPostResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuthorsLastFivePosts> getLastFivePostsOfAuthor(String username) {
        userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("Author", "username", username)
        );

        List<Post> posts = postRepository.findFiveByAuthorUsernameAndActiveTrueOrderByCreateDateTimeDesc(username);

        return posts
                .stream()
                .map(postMapper::postToAuthorsLastFivePosts)
                .collect(Collectors.toList());

    }

    @Override
    public Integer countsByCategoryName(String categoryName) {
        categoryRepository.findByName(categoryName).orElseThrow(
                () -> new ResourceNotFoundException("Category", "name", categoryName)
        );
        return postRepository.countByCategoryNameAndActiveTrue(categoryName);
    }

    private Post getPost(String id) throws AccessDeniedException {

        Post post = postRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", id)
        );
        checkAuthorization.checkUser(post.getAuthor());

        return post;
    }

    private void validateAndSanitizeFieldName(String fieldName, String fieldValue) {
        Assert.notNull(fieldValue, "Provide a valid " + fieldName + " , please.");
        if (fieldValue.isEmpty() || fieldValue.isBlank()) {
            throw new IllegalArgumentException("Provide a valid " + fieldName + " , please.");
        }
    }

}
