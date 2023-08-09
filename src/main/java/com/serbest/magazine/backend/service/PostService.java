package com.serbest.magazine.backend.service;

import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.dto.post.*;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@Service
public interface PostService {
    PostCreateResponseDTO createPost(PostRequestDTO requestDTO);
    PostCreateResponseDTO createPostEditor(PostCreateEditorRequestDTO requestDTO) throws IOException;
    Map<String, Object> getAllPosts(Integer page , Integer size);
    List<FirstFivePostsListDTO> getFirstFivePosts();
    List<MainPagePostsListDTO> getFourPostsForTop();
    List<MainPagePostsListDTO> getPostsForMainPage();
    List<DeactivatedPostApiResponseDTO> getDeactivatedPost();
    PostResponseDTO findById(String id);
    PostResponseDTO updatePost(String id, PostUpdateRequestDTO requestDTO) throws IOException;
    PostResponseDTO updatePostEditor(String id, PostUpdateEditorRequestDTO requestDTO) throws IOException;
    PostResponseDTO deactivatePost(String id) throws AccessDeniedException;
    PostResponseDTO activatePost(String id);
    MessageResponseDTO deletePost(String id);
    List<PostResponseDTO> getRandomThreePost();
    Map<String, Object> getPostsByCategory(String categoryName,Integer page,Integer size);
    Map<String, Object> getPostsBySubCategory(String subCategoryId,Integer page,Integer size);
    List<AuthorsLastFivePosts> getLastFivePostsOfAuthor(String username);
    List<PlaylistPostListResponseDTO> getPostsByPlaylist(String playlistId);
    List<PostsOfAuthorForPlaylistResponseDTO> getPostsOfAuthorForPlaylist(String username,String playlistId);
    List<PostResponseDTO> searchPosts(String keyword);
    Map<String, Object> findByUsername(String userId, Integer page, Integer size);
    Integer countsByCategoryName(String categoryName);
}
