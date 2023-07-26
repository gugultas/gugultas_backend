package com.serbest.magazine.backend.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequestDTO {

    @NotBlank(message = "Please provide a title.")
    @Size(max = 60, message = "Title size cannot be more than 60 characters.")
    private String title;

    private String subtitle;

    @NotBlank(message = "Please provide your content.")
    @Size(min = 250, message = "Content size cannot be less than 250 characters.")
    private String content;

    @NotBlank(message = "Please provide a category.")
    private String category;

    @NotBlank(message = "Please provide a sub-category.")
    private String subCategory;

    private MultipartFile image;

}
