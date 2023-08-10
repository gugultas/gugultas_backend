package com.serbest.magazine.backend.service.impl;

import com.serbest.magazine.backend.common.validation.StringValidationCommon;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.entity.*;
import com.serbest.magazine.backend.repository.*;
import com.serbest.magazine.backend.service.ImageModelService;
import com.serbest.magazine.backend.service.PostService;
import com.serbest.magazine.backend.dto.post.*;
import com.serbest.magazine.backend.exception.CustomApplicationException;
import com.serbest.magazine.backend.exception.ResourceNotFoundException;
import com.serbest.magazine.backend.mapper.PostMapper;
import com.serbest.magazine.backend.security.CheckAuthorization;
import com.serbest.magazine.backend.util.UploadImage;

import io.jsonwebtoken.lang.Assert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final CheckAuthorization checkAuthorization;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final AuthorRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final ImageModelService imageModelService;

    public PostServiceImpl(CheckAuthorization checkAuthorization, CategoryRepository categoryRepository,
                           SubCategoryRepository subCategoryRepository, PostRepository postRepository,
                           PostMapper postMapper, AuthorRepository userRepository, PlaylistRepository playlistRepository,
                           ImageModelService imageModelService) {
        this.checkAuthorization = checkAuthorization;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.imageModelService = imageModelService;
    }

    @Override
    public PostCreateResponseDTO createPost(PostRequestDTO requestDTO) {
        validateAndSanitizeFieldName("Title", requestDTO.getTitle());
        validateAndSanitizeFieldName("Content", requestDTO.getContent());
        validateAndSanitizeFieldName("Category", requestDTO.getCategory());
        validateAndSanitizeFieldName("SubCategory", requestDTO.getSubCategory());

        StringValidationCommon.common_validateStringLength(1, 60, requestDTO.getTitle());

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

        if (!user.isEnabled()) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST,
                    "Hesabınız aktive edilmemiştir.");
        }

        if (!category.getName().equals(subCategory.getCategory().getName())) {
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

        StringValidationCommon.common_validateStringLength(1, 60, requestDTO.getTitle());

        Author user = userRepository.findByUsername(requestDTO.getAuthor()).orElseThrow(
                () -> new ResourceNotFoundException("Author", "username", requestDTO.getAuthor())
        );

        Category category = categoryRepository.findByName(requestDTO.getCategory()).orElseThrow(
                () -> new ResourceNotFoundException("Category", "name", requestDTO.getCategory())
        );

        SubCategory subCategory = subCategoryRepository.findByName(requestDTO.getSubCategory()).orElseThrow(
                () -> new ResourceNotFoundException("Sub-Category", "name", requestDTO.getSubCategory())
        );

        if (!user.isEnabled()) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST,
                    "Hesabınız aktive edilmemiştir.");
        }

        if (!category.getName().equals(subCategory.getCategory().getName())) {
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
    public Map<String, Object> getAllPosts(Integer page, Integer size) {

        Pageable paging = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findByActiveTrueOrderByCreateDateTimeDesc(paging);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts.getContent().stream().map(postMapper::postToPostResponseDTO).collect(Collectors.toList()));
        response.put("currentPage", posts.getNumber());
        response.put("totalItems", posts.getTotalElements());
        response.put("totalPages", posts.getTotalPages());

        return response;
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

        StringValidationCommon.common_validateStringLength(1, 60, requestDTO.getTitle());

        Post post = getPost(id);

        Category category = categoryRepository.findByName(requestDTO.getCategory()).orElseThrow(
                () -> new ResourceNotFoundException("Category", "name", requestDTO.getCategory())
        );

        SubCategory subCategory = subCategoryRepository.findByName(requestDTO.getSubCategory()).orElseThrow(
                () -> new ResourceNotFoundException("Sub-Category", "name", requestDTO.getSubCategory())
        );

        if (!category.getName().equals(subCategory.getCategory().getName())) {
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
    public MessageResponseDTO deletePost(String id) {
        Post post = postRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", id)
        );

        postRepository.deleteById(post.getPostId());

        return new MessageResponseDTO(post.getTitle() + " başlıklı postunuz başırılı bir şekilde silinmiştir.");
    }

    @Override
    public PostResponseDTO updatePostEditor(String id, PostUpdateEditorRequestDTO requestDTO) {
        validateAndSanitizeFieldName("Title", requestDTO.getTitle());
        validateAndSanitizeFieldName("Content", requestDTO.getContent());
        validateAndSanitizeFieldName("Category", requestDTO.getCategory());
        validateAndSanitizeFieldName("Sub-Category", requestDTO.getSubCategory());

        StringValidationCommon.common_validateStringLength(1, 60, requestDTO.getTitle());

        Post post = postRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", id)
        );

        Category category = categoryRepository.findByName(requestDTO.getCategory()).orElseThrow(
                () -> new ResourceNotFoundException("Category", "name", requestDTO.getCategory())
        );

        SubCategory subCategory = subCategoryRepository.findByName(requestDTO.getSubCategory()).orElseThrow(
                () -> new ResourceNotFoundException("Sub-Category", "name", requestDTO.getSubCategory())
        );

        if (!category.getName().equals(subCategory.getCategory().getName())) {
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
            post.setDescription(requestDTO.getDescription());
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
    public Map<String, Object> getPostsByCategory(String categoryName, Integer page, Integer size) {
        categoryRepository.findByName(categoryName).orElseThrow(
                () -> new ResourceNotFoundException("Category", "name", categoryName)
        );

        Pageable paging = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findAllByCategoryNameAndActiveTrueOrderByCreateDateTimeDesc(categoryName, paging);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts.getContent().stream().map(postMapper::postToPostResponseDTO).collect(Collectors.toList()));
        response.put("currentPage", posts.getNumber());
        response.put("totalItems", posts.getTotalElements());
        response.put("totalPages", posts.getTotalPages());

        return response;
    }

    @Override
    public Map<String, Object> getPostsBySubCategory(String subCategoryId, Integer page, Integer size) {
        SubCategory subCategory = subCategoryRepository.findById(UUID.fromString(subCategoryId)).orElseThrow(
                () -> new ResourceNotFoundException("Sub Category", "id", subCategoryId)
        );

        Pageable paging = PageRequest.of(page, size);

        Page<Post> posts =
                postRepository
                        .findAllBySubCategoryIdAndActiveTrueOrderByCreateDateTimeDesc(UUID.fromString(subCategoryId), paging);
        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts.getContent().stream().map(postMapper::postToPostResponseDTO).collect(Collectors.toList()));
        response.put("currentPage", posts.getNumber());
        response.put("totalItems", posts.getTotalElements());
        response.put("totalPages", posts.getTotalPages());
        response.put("title", subCategory.getName());

        return response;
    }

    @Override
    public List<PostResponseDTO> searchPosts(String keyword) {
        return postRepository
                .findByActiveTrueAndTitleContainingIgnoreCase(keyword)
                .stream()
                .map(postMapper::postToPostResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> findByUsername(String username, Integer page, Integer size) {
        userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("Author", "username", username)
        );

        Pageable paging = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findAllByAuthorUsernameAndActiveTrueOrderByCreateDateTimeDesc(username, paging);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts.getContent().stream().map(postMapper::postToPostResponseDTO).collect(Collectors.toList()));
        response.put("currentPage", posts.getNumber());
        response.put("totalItems", posts.getTotalElements());
        response.put("totalPages", posts.getTotalPages());

        return response;
    }

    @Override
    public List<AuthorsLastFivePosts> getLastFivePostsOfAuthor(String username) {
        userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("Author", "username", username)
        );

        List<Post> posts = postRepository.findTop5ByAuthorUsernameAndActiveTrueOrderByCreateDateTimeDesc(username);

        return posts
                .stream()
                .map(postMapper::postToAuthorsLastFivePosts)
                .collect(Collectors.toList());

    }

    @Override
    public List<PlaylistPostListResponseDTO> getPostsByPlaylist(String playlistId) {
        List<Playlist> playlists = new ArrayList<>();

        Playlist playlist = playlistRepository.findById(UUID.fromString(playlistId)).orElseThrow(
                () -> new ResourceNotFoundException("Playlist", "id", playlistId)
        );

        playlists.add(playlist);

        List<Post> posts = postRepository.findByPlaylistsInOrderByCreateDateTimeDesc(playlists);

        return posts
                .stream()
                .map(postMapper::postToPlaylistPostListResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostsOfAuthorForPlaylistResponseDTO> getPostsOfAuthorForPlaylist(String username, String playlistId) {
        Playlist playlist = playlistRepository.findById(UUID.fromString(playlistId)).orElseThrow(
                () -> new ResourceNotFoundException("Playlist", "id", playlistId)
        );

        return postRepository
                .findAllByAuthorUsernameAndActiveTrueOrderByCreateDateTimeDesc(username)
                .stream()
                .filter(post -> !post.getPlaylists().contains(playlist))
                .map(post -> new PostsOfAuthorForPlaylistResponseDTO(post.getPostId(), post.getTitle()))
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
