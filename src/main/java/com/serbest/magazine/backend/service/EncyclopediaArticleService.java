package com.serbest.magazine.backend.service;

import com.serbest.magazine.backend.dto.encyclopediaArticle.EncyclopediaArticleRequestDTO;
import com.serbest.magazine.backend.dto.encyclopediaArticle.EncyclopediaArticleResponseDTO;
import com.serbest.magazine.backend.dto.encyclopediaArticle.EncyclopediaArticleUpdateRequestDTO;
import com.serbest.magazine.backend.dto.encyclopediaArticle.EncyclopediaArticleUpdateResponseDTO;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;

import java.util.List;
public interface EncyclopediaArticleService {

    MessageResponseDTO createEncyclopediaArticle(EncyclopediaArticleRequestDTO encyclopediaArticleRequestDTO);

    EncyclopediaArticleUpdateResponseDTO updateEncyclopediaArticle(String id, EncyclopediaArticleUpdateRequestDTO encyclopediaArticleUpdateRequestDTO);

    MessageResponseDTO deleteEncyclopediaArticle(String id);

    EncyclopediaArticleResponseDTO getEncyclopediaArticleById(String encyclopediaArticleId);

    List<EncyclopediaArticleResponseDTO> getLastSevenEncyclopediaArticle();

    List<EncyclopediaArticleResponseDTO> getAllEncyclopediaArticles();

}
