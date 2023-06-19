package com.serbest.magazine.backend.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterpieceOfTheWeekResponseDTO {

    private UUID id;

    private String title;

    private String owner;

    private String info;

    private String showLink;

    private String showLink2;

    private String marketLink;

    private String image;

    private LocalDateTime createDateTime;

    private LocalDateTime updateDateTime;

}
