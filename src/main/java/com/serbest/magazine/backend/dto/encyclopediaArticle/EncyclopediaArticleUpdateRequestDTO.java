package com.serbest.magazine.backend.dto.encyclopediaArticle;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EncyclopediaArticleUpdateRequestDTO {

    @NotBlank(message = "Lütfen en fazla 75 karakter içeren bir başlık giriniz.")
    @Length(max = 75, message = "Başlık 75 karakterden fazla olamaz.")
    private String title;

    @NotBlank(message = "Lütfen geçerli bir içerik giriniz.")
    private String content;
}
