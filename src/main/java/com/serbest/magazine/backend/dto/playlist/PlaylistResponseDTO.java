package com.serbest.magazine.backend.dto.playlist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlaylistResponseDTO {

    private UUID id;
    private String title;
    private String description;
    private UUID playlistImage;
    private String playlistImageType;
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;
}
