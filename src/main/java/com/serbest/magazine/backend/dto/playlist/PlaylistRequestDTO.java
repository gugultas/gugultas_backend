package com.serbest.magazine.backend.dto.playlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlaylistRequestDTO {

    @NotBlank(message = "Please provide a title.")
    @Size(max = 75, message = "Title size cannot be more than 75 characters.")
    private String title;

    private String description;

    private MultipartFile playlistImage;
}
