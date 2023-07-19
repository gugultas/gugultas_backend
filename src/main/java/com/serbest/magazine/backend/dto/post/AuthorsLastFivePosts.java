package com.serbest.magazine.backend.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorsLastFivePosts {

    private UUID postId;
    private String title;
    private LocalDateTime createdTime;

}
