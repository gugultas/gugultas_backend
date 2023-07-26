package com.serbest.magazine.backend.dto.post;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostsOfAuthorForPlaylistResponseDTO {
    private UUID postId;
    private String title;
}
