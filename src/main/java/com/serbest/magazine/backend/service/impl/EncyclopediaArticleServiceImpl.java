package com.serbest.magazine.backend.service.impl;

import com.serbest.magazine.backend.common.validation.StringValidationCommon;
import com.serbest.magazine.backend.dto.encyclopediaArticle.EncyclopediaArticleRequestDTO;
import com.serbest.magazine.backend.dto.encyclopediaArticle.EncyclopediaArticleResponseDTO;
import com.serbest.magazine.backend.dto.encyclopediaArticle.EncyclopediaArticleUpdateRequestDTO;
import com.serbest.magazine.backend.dto.encyclopediaArticle.EncyclopediaArticleUpdateResponseDTO;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.entity.EncyclopediaArticle;
import com.serbest.magazine.backend.exception.ResourceNotFoundException;
import com.serbest.magazine.backend.mapper.EncyclopediaArticleMapper;
import com.serbest.magazine.backend.repository.EncyclopediaArticleRepository;
import com.serbest.magazine.backend.service.EncyclopediaArticleService;
import io.jsonwebtoken.lang.Assert;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EncyclopediaArticleServiceImpl implements EncyclopediaArticleService {

    private final EncyclopediaArticleRepository encyclopediaArticleRepository;
    private final EncyclopediaArticleMapper encyclopediaArticleMapper;

    public EncyclopediaArticleServiceImpl(EncyclopediaArticleRepository encyclopediaArticleRepository, EncyclopediaArticleMapper encyclopediaArticleMapper) {
        this.encyclopediaArticleRepository = encyclopediaArticleRepository;
        this.encyclopediaArticleMapper = encyclopediaArticleMapper;
    }

    @Override
    public MessageResponseDTO createEncyclopediaArticle(EncyclopediaArticleRequestDTO encyclopediaArticleRequestDTO) {
        checkValidateAndSanitizeInput("title", encyclopediaArticleRequestDTO.getTitle());
        checkValidateAndSanitizeInput("content", encyclopediaArticleRequestDTO.getContent());

        StringValidationCommon.common_validateStringLength(1, 75, encyclopediaArticleRequestDTO.getTitle());

        EncyclopediaArticle encyclopediaArticle = new EncyclopediaArticle(
                encyclopediaArticleRequestDTO.getTitle(),
                encyclopediaArticleRequestDTO.getContent(),
                encyclopediaArticleRequestDTO.getDescription());

        EncyclopediaArticle newEncyclopediaArticle = encyclopediaArticleRepository.save(encyclopediaArticle);

        return new MessageResponseDTO( newEncyclopediaArticle.getTitle() + " başlıklı yeni bilgi eklenmiştir. ");
    }

    @Override
    public EncyclopediaArticleUpdateResponseDTO updateEncyclopediaArticle(String id,
            EncyclopediaArticleUpdateRequestDTO updateRequestDTO) {
        checkValidateAndSanitizeInput("title", updateRequestDTO.getTitle());
        checkValidateAndSanitizeInput("content", updateRequestDTO.getContent());

        StringValidationCommon.common_validateStringLength(1, 75, updateRequestDTO.getTitle());

        EncyclopediaArticle encyclopediaArticle = encyclopediaArticleRepository.findById(UUID.fromString(id))
                .orElseThrow(
                        () -> new ResourceNotFoundException("EncyclopediaArticle","id",id)
                );

        encyclopediaArticle.setTitle(updateRequestDTO.getTitle());
        encyclopediaArticle.setContent(updateRequestDTO.getContent());
        encyclopediaArticle.setDescription(updateRequestDTO.getDescription());

        EncyclopediaArticle newEncyclopediaArticle = encyclopediaArticleRepository.save(encyclopediaArticle);

        return encyclopediaArticleMapper.encyclopediaArticleToEncyclopediaArticleUpdateResponseDTO(newEncyclopediaArticle);
    }

    @Override
    public MessageResponseDTO deleteEncyclopediaArticle(String id) {
        EncyclopediaArticle encyclopediaArticle = encyclopediaArticleRepository.findById(UUID.fromString(id))
                .orElseThrow(
                        () -> new ResourceNotFoundException("EncyclopediaArticle","id",id)
                );

        encyclopediaArticleRepository.deleteById(encyclopediaArticle.getId());

        return new MessageResponseDTO(id + " ID'li bilgi başarıyla silinmiştir.");
    }

    @Override
    public EncyclopediaArticleResponseDTO getEncyclopediaArticleById(String encyclopediaArticleId) {
        checkValidateAndSanitizeInput("id", encyclopediaArticleId);

        EncyclopediaArticle encyclopediaArticle =
                encyclopediaArticleRepository
                        .findById(UUID.fromString(encyclopediaArticleId))
                        .orElseThrow(
                                () -> new ResourceNotFoundException("EncyclopediaArticle","id",encyclopediaArticleId)
                        );

        return encyclopediaArticleMapper.encyclopediaArticleToEncyclopediaArticleResponseDTO(encyclopediaArticle);
    }

    @Override
    public List<EncyclopediaArticleResponseDTO> getLastSevenEncyclopediaArticle() {
        return encyclopediaArticleRepository
                .findTop7ByOrderByCreateDateTimeDesc()
                .stream().map(encyclopediaArticleMapper::encyclopediaArticleToEncyclopediaArticleResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EncyclopediaArticleResponseDTO> getAllEncyclopediaArticles() {
        return encyclopediaArticleRepository
                .findAllByOrderByCreateDateTimeDesc()
                .stream().map(encyclopediaArticleMapper::encyclopediaArticleToEncyclopediaArticleResponseDTO)
                .collect(Collectors.toList());
    }

    private void checkValidateAndSanitizeInput(String fieldName, String fieldValue) {
        Assert.notNull(fieldValue, "Please , provide a valid " + fieldName + ".");
        if (fieldValue.isEmpty() || fieldValue.isBlank()) {
            throw new IllegalArgumentException("Please , provide a valid " + fieldName + ".");
        }
    }
}
