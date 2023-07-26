package com.serbest.magazine.backend.dto.playlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemovePostFromPlaylistDTO {
    private String playlistId;
    private String postId;
}
