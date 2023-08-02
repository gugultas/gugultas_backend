package com.serbest.magazine.backend.common.dto;

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
public class MasterpieceListResponseDTO {
    private UUID id;

    private String title;

    private String owner;

    private LocalDateTime createDateTime;
}
