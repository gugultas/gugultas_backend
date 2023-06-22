package com.serbest.magazine.backend.mapper;

import com.serbest.magazine.backend.dto.post.*;
import com.serbest.magazine.backend.entity.ImageModel;
import com.serbest.magazine.backend.entity.Post;

import com.serbest.magazine.backend.util.UploadImage;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class PostMapper {

    public PostResponseDTO postToPostResponseDTO(Post post) {
        return PostResponseDTO.builder()
                .id(post.getPostId())
                .title(post.getTitle())
                .subtitle(post.getSubtitle())
                .content(post.getContent())
                .category(post.getCategory().getName())
                .subCategory(post.getSubCategory().getName())
                .image(post.getPostImage().getId())
                .username(post.getAuthor().getUsername())
                .profileImageId(post.getAuthor().getProfileImage().getId())
                .profileImageType(post.getAuthor().getProfileImage().getType())
                .comments(post.getComments().stream().count())
                .createDateTime(post.getCreateDateTime())
                .updateDateTime(post.getUpdateDateTime())
                .build();
    }

    public Post postRequestDTOToPost(PostRequestDTO postRequestDTO) throws IOException {
        return Post.Builder
                .newBuilder()
                .title(postRequestDTO.getTitle())
                .subtitle(postRequestDTO.getSubtitle())
                .content(postRequestDTO.getContent())
                .postImage(UploadImage.uploadImage(postRequestDTO.getImage()))
                .active(true)
                .build();
    }

    public Post postCreateEditorRequestDTOToPost(PostCreateEditorRequestDTO postRequestDTO) throws IOException {
        return Post.Builder
                .newBuilder()
                .title(postRequestDTO.getTitle())
                .subtitle(postRequestDTO.getSubtitle())
                .content(postRequestDTO.getContent())
                .postImage(UploadImage.uploadImage(postRequestDTO.getImage()))
                .active(true)
                .build();
    }

    public PostCreateResponseDTO postToPostCreateResponseDTO(Post post) {
        return PostCreateResponseDTO.builder()
                .id(post.getPostId())
                .title(post.getTitle())
                .subtitle(post.getSubtitle())
                .content(post.getContent())
                .createDateTime(post.getCreateDateTime())
                .updateDateTime(post.getUpdateDateTime())
                .build();
    }

    public FirstFivePostsListDTO postToFirstFivePostsListDTO(Post post) {
        return new FirstFivePostsListDTO(
                post.getPostId(), post.getPostImage().getId(), post.getTitle(), post.getAuthor().getProfileImage().getId()
                , post.getAuthor().getProfileImage().getType());
    }

    public MainPagePostsListDTO postToMainPagePostsListDTO(Post post) {
        return MainPagePostsListDTO.builder()
                .id(post.getPostId())
                .title(post.getTitle())
                .category(post.getCategory().getName())
                .subCategory(post.getSubCategory().getName())
                .username(post.getAuthor().getUsername())
                .image(post.getPostImage().getId())
                .comments(post.getComments().stream().count())
                .createDateTime(post.getCreateDateTime())
                .build();
    }

    public DeactivatedPostApiResponseDTO postToDeactivatedPostApiResponseDTO(Post post) {
        return DeactivatedPostApiResponseDTO.builder()
                .id(post.getPostId())
                .title(post.getTitle())
                .category(post.getCategory().getName())
                .username(post.getAuthor().getUsername())
                .build();
    }


}
