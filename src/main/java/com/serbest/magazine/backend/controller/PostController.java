package com.serbest.magazine.backend.controller;

import com.serbest.magazine.backend.dto.post.*;
import com.serbest.magazine.backend.service.PostService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "https://gugultas.com"}, maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<PostCreateResponseDTO> createPost(@Valid @ModelAttribute PostRequestDTO requestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(requestDTO));
    }

    @PreAuthorize("hasRole('ROLE_EDITOR')")
    @PostMapping(value = "/editor/createPost", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<PostCreateResponseDTO> createPostEditor(@Valid @ModelAttribute PostCreateEditorRequestDTO requestDTO) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPostEditor(requestDTO));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PostResponseDTO>> getAllPost() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping(value = "/firstFivePosts",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FirstFivePostsListDTO>> getFirstFivePosts() {
        return ResponseEntity.ok(postService.getFirstFivePosts());
    }

    @GetMapping(value = "/fourPostsForTop",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MainPagePostsListDTO>> getFourPostsForTop() {
        return ResponseEntity.ok(postService.getFourPostsForTop());
    }

    @GetMapping(value = "/mainPagePosts",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MainPagePostsListDTO>> getPostsForMainPage() {
        return ResponseEntity.ok(postService.getPostsForMainPage());
    }

    @GetMapping("/getSinglePostBy/{postId}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable String postId){
        return ResponseEntity.ok(postService.findById(postId));
    }

    @GetMapping("/getPostsByAuthor/{username}")
    public ResponseEntity<List<PostResponseDTO>> getPostsByUsername(@PathVariable String username){
        return ResponseEntity.ok(postService.findByUsername(username));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getDeactivatedPosts")
    public ResponseEntity<List<DeactivatedPostApiResponseDTO>> getDeactivatedPosts(){
        return ResponseEntity.ok(postService.getDeactivatedPost());
    }

    @PreAuthorize("hasAnyRole('ROLE_AUTHOR','ROLE_EDITOR','ROLE_ADMIN')")
    @PutMapping("/deactivatePost/{id}")
    public ResponseEntity<PostResponseDTO> deactivatePost(@PathVariable String id) throws AccessDeniedException {
        return ResponseEntity.ok(postService.deactivatePost(id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/activatePost/{id}")
    public ResponseEntity<PostResponseDTO> activatePost(@PathVariable String id){
        return ResponseEntity.ok(postService.activatePost(id));
    }

    @PutMapping(value = "/updatePost/{id}",consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable String id,@Valid @ModelAttribute PostUpdateRequestDTO requestDTO)
            throws IOException {
        return ResponseEntity.ok(postService.updatePost(id,requestDTO));
    }

    @PreAuthorize("hasRole('ROLE_EDITOR')")
    @PutMapping(value = "/editor/updatePost/{id}",consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<PostResponseDTO> updatePostForEditor(@PathVariable String id,@Valid @ModelAttribute PostUpdateEditorRequestDTO requestDTO)
            throws IOException {
        return ResponseEntity.ok(postService.updatePostEditor(id,requestDTO));
    }

    @GetMapping("/getPostByCategory/{category}")
    public ResponseEntity<List<PostResponseDTO>> getAllPostByCategory(@PathVariable String category){
        return ResponseEntity.ok(postService.getPostsByCategory(category));
    }

    @GetMapping("/getPostBySubCategory/{subCategory}")
    public ResponseEntity<List<PostResponseDTO>> getAllPostBySubCategory(@PathVariable String subCategory){
        return ResponseEntity.ok(postService.getPostsBySubCategory(subCategory));
    }

    @GetMapping("/getLastFivePostsByAuthor/{username}")
    public ResponseEntity<List<AuthorsLastFivePosts>> getLastFivePostsByAuthor(@PathVariable String username){
        return ResponseEntity.ok(postService.getLastFivePostsOfAuthor(username));
    }

    @GetMapping("/getPostsByPlaylist/{id}")
    public ResponseEntity<List<PlaylistPostListResponseDTO>> getPostsByPlaylist(@PathVariable String id){
        return ResponseEntity.ok(postService.getPostsByPlaylist(id));
    }

    @GetMapping("/getPostsOfAuthorForPlaylist/{username}/{playlistId}")
    public ResponseEntity<List<PostsOfAuthorForPlaylistResponseDTO>>
        getPostsOfAuthorForPlaylist(@PathVariable String username , @PathVariable String playlistId){
        return ResponseEntity.ok(postService.getPostsOfAuthorForPlaylist(username,playlistId));
    }

    @GetMapping("/searchPosts/{title}")
    public ResponseEntity<List<PostResponseDTO>> searchPosts(@PathVariable String title){
        return ResponseEntity.ok(postService.searchPosts(title));
    }

    @GetMapping("/randomThree")
    public ResponseEntity<List<PostResponseDTO>> getThreeByRandomPosts(){
        return ResponseEntity.ok(postService.getRandomThreePost());
    }

    @GetMapping("/countsByCategory/{categoryName}")
    public ResponseEntity<Integer> countsByCategoryName(@PathVariable String categoryName){
        return ResponseEntity.ok(postService.countsByCategoryName(categoryName));
    }

}
