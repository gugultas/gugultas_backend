package com.serbest.magazine.backend.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterpieceRequestDTO {

    @NotBlank(message = "Please provide a title.")
    @Size(max = 75, message = "Title size cannot be more than 75 characters.")
    private String title;

    @NotBlank(message = "Please provide a owner.")
    private String owner;

    private String info;

    private String showLink;

    private String showLink2;

    private String marketLink;

    private MultipartFile image;
}
