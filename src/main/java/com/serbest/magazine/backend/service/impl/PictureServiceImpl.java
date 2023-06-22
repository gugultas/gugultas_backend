package com.serbest.magazine.backend.service.impl;

import com.serbest.magazine.backend.common.dto.MasterpieceOfTheWeekResponseDTO;
import com.serbest.magazine.backend.common.dto.MasterpieceRequestDTO;
import com.serbest.magazine.backend.common.dto.MasterpieceResponseDTO;
import com.serbest.magazine.backend.common.dto.MasterpieceUpdateRequestDTO;
import com.serbest.magazine.backend.common.mapper.MasterpieceMapper;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.entity.Picture;
import com.serbest.magazine.backend.exception.CustomApplicationException;
import com.serbest.magazine.backend.exception.ResourceNotFoundException;
import com.serbest.magazine.backend.repository.PictureRepository;
import com.serbest.magazine.backend.service.ImageModelService;
import com.serbest.magazine.backend.service.PictureService;
import com.serbest.magazine.backend.util.UploadImage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class PictureServiceImpl implements PictureService {

    private final PictureRepository pictureRepository;
    private final MasterpieceMapper masterpieceMapper;
    private final ImageModelService imageModelService;

    public PictureServiceImpl(PictureRepository pictureRepository, MasterpieceMapper masterpieceMapper, ImageModelService imageModelService) {
        this.pictureRepository = pictureRepository;
        this.masterpieceMapper = masterpieceMapper;
        this.imageModelService = imageModelService;
    }

    @Override
    public MessageResponseDTO create(MasterpieceRequestDTO requestDTO) {
        Picture picture = null;

        try {

            picture = masterpieceMapper.masterpieceRequestDTOToPicture(requestDTO);

            Picture savedPicture = pictureRepository.save(picture);

            return new MessageResponseDTO(savedPicture.getTitle() + " adlı resim başarıyla eklenmiştir.");

        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public MasterpieceOfTheWeekResponseDTO getMasterpieceOfTheWeek() {
        return masterpieceMapper
                .masterpieceToMasterpieceOfTheWeekResponseDTO(pictureRepository.findTopByOrderByCreateDateTimeDesc());
    }

    @Override
    public MasterpieceResponseDTO getMasterpieceById(String id) {
        Picture picture = pictureRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new ResourceNotFoundException("Picture", "id", id)
        );
        return masterpieceMapper
                .masterpieceToMasterpieceResponseDTO(picture);
    }

    @Override
    public MasterpieceResponseDTO updateMasterpiece(String id, MasterpieceUpdateRequestDTO requestDTO) {

        Picture picture = pictureRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new ResourceNotFoundException("picture", "id", id)
        );

        try {
            if (!requestDTO.getImageProtect()) {
                picture.setImage(UploadImage.uploadImage(requestDTO.getImage()));
            }
            picture.setTitle(requestDTO.getTitle());
            picture.setOwner(requestDTO.getOwner());
            picture.setInfo(requestDTO.getInfo());
            picture.setShowLink(requestDTO.getShowLink());
            picture.setShowLink2(requestDTO.getShowLink2());
            picture.setMarketLink(requestDTO.getMarketLink());

            return masterpieceMapper.masterpieceToMasterpieceResponseDTO(pictureRepository.save(picture));

        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }
}
