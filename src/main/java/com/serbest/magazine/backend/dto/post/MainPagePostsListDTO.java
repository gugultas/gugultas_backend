package com.serbest.magazine.backend.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainPagePostsListDTO {
    private UUID id;
    private String title;
    private String category;
    private String subCategory;
    private String username;
    private UUID image;
    private Long comments;
    private LocalDateTime createDateTime;
}
