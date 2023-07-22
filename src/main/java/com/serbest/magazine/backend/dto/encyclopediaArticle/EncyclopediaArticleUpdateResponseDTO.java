package com.serbest.magazine.backend.dto.encyclopediaArticle;

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
public class EncyclopediaArticleUpdateResponseDTO {

    private UUID id;
    private String title;
    private String content;
    private LocalDateTime createDateTime;

}
