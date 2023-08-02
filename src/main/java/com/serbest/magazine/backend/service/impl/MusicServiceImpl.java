package com.serbest.magazine.backend.service.impl;

import com.serbest.magazine.backend.common.dto.*;
import com.serbest.magazine.backend.common.entity.Masterpiece;
import com.serbest.magazine.backend.common.mapper.MasterpieceMapper;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.entity.Music;
import com.serbest.magazine.backend.exception.CustomApplicationException;
import com.serbest.magazine.backend.exception.ResourceNotFoundException;
import com.serbest.magazine.backend.repository.MusicRepository;
import com.serbest.magazine.backend.service.ImageModelService;
import com.serbest.magazine.backend.service.MusicService;
import com.serbest.magazine.backend.util.UploadImage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MusicServiceImpl implements MusicService {

    private final MusicRepository musicRepository;
    private final MasterpieceMapper masterpieceMapper;
    private final ImageModelService imageModelService;

    public MusicServiceImpl(MusicRepository musicRepository, MasterpieceMapper masterpieceMapper, ImageModelService imageModelService) {
        this.musicRepository = musicRepository;
        this.masterpieceMapper = masterpieceMapper;
        this.imageModelService = imageModelService;
    }

    @Override
    public MessageResponseDTO create(MasterpieceRequestDTO requestDTO) {
        Music music = null;
        try {
            music = masterpieceMapper.masterpieceRequestDTOToMusic(requestDTO);

            Music savedMusic = musicRepository.save(music);

            return new MessageResponseDTO(savedMusic.getTitle() + " adlı albüm başarıyla eklenmiştir.");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public MasterpieceOfTheWeekResponseDTO getMasterpieceOfTheWeek() {
        return masterpieceMapper
                .masterpieceToMasterpieceOfTheWeekResponseDTO(musicRepository.findTopByOrderByCreateDateTimeDesc());
    }

    @Override
    public List<MasterpieceListResponseDTO> getMasterpieces() {
        return musicRepository
                .findAllByOrderByCreateDateTimeDesc()
                .stream()
                .map(masterpieceMapper::masterpieceToMasterpieceListResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MasterpieceResponseDTO getMasterpieceById(String id) {
        Music music = musicRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new ResourceNotFoundException("Music", "id", id)
        );
        return masterpieceMapper
                .masterpieceToMasterpieceResponseDTO(music);
    }

    @Override
    public MasterpieceResponseDTO updateMasterpiece(String id, MasterpieceUpdateRequestDTO requestDTO) {

        Music music = musicRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new ResourceNotFoundException("Music", "id", id)
        );

        try {
            if (!requestDTO.getImageProtect()) {
                music.setImage(UploadImage.uploadImage(requestDTO.getImage()));
            }
            music.setTitle(requestDTO.getTitle());
            music.setOwner(requestDTO.getOwner());
            music.setInfo(requestDTO.getInfo());
            music.setShowLink(requestDTO.getShowLink());
            music.setShowLink2(requestDTO.getShowLink2());
            music.setMarketLink(requestDTO.getMarketLink());

            return masterpieceMapper.masterpieceToMasterpieceResponseDTO(musicRepository.save(music));

        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }
}
