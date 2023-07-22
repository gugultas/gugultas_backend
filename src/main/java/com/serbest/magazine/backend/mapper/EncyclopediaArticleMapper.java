package com.serbest.magazine.backend.mapper;

import com.serbest.magazine.backend.dto.encyclopediaArticle.EncyclopediaArticleResponseDTO;
import com.serbest.magazine.backend.dto.encyclopediaArticle.EncyclopediaArticleUpdateResponseDTO;
import com.serbest.magazine.backend.entity.EncyclopediaArticle;
import org.springframework.stereotype.Component;

@Component
public class EncyclopediaArticleMapper {

    public EncyclopediaArticleUpdateResponseDTO
        encyclopediaArticleToEncyclopediaArticleUpdateResponseDTO(EncyclopediaArticle encyclopediaArticle ){
        return new EncyclopediaArticleUpdateResponseDTO(
                encyclopediaArticle.getId(),
                encyclopediaArticle.getTitle(),
                encyclopediaArticle.getContent(),
                encyclopediaArticle.getCreateDateTime());
    }

    public EncyclopediaArticleResponseDTO encyclopediaArticleToEncyclopediaArticleResponseDTO(
            EncyclopediaArticle encyclopediaArticle){
        return new EncyclopediaArticleResponseDTO(
                encyclopediaArticle.getId(),
                encyclopediaArticle.getTitle(),
                encyclopediaArticle.getContent(),
                encyclopediaArticle.getCreateDateTime()
        );
    }
}
