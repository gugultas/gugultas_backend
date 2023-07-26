package com.serbest.magazine.backend.dto.playlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPostToPlaylistDTO {
    private String playlistId;
    private String postId;
}
