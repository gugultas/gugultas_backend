package com.serbest.magazine.backend.common.mapper;

import com.serbest.magazine.backend.common.dto.MasterpieceOfTheWeekResponseDTO;
import com.serbest.magazine.backend.common.dto.MasterpieceRequestDTO;
import com.serbest.magazine.backend.common.dto.MasterpieceResponseDTO;
import com.serbest.magazine.backend.common.entity.Masterpiece;
import com.serbest.magazine.backend.entity.Movie;
import com.serbest.magazine.backend.entity.Music;
import com.serbest.magazine.backend.entity.Picture;
import com.serbest.magazine.backend.util.UploadImage;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MasterpieceMapper {

    public Music masterpieceRequestDTOToMusic (MasterpieceRequestDTO requestDTO) throws IOException {
        return Music.Builder.newBuilder()
                .title(requestDTO.getTitle())
                .owner(requestDTO.getOwner())
                .info(requestDTO.getInfo())
                .image(UploadImage.uploadImage(requestDTO.getImage()))
                .showLink(requestDTO.getShowLink())
                .showLink2(requestDTO.getShowLink2())
                .marketLink(requestDTO.getMarketLink())
                .build();
    }

    public Picture masterpieceRequestDTOToPicture (MasterpieceRequestDTO requestDTO) throws IOException {
        return Picture.Builder.newBuilder()
                .title(requestDTO.getTitle())
                .owner(requestDTO.getOwner())
                .info(requestDTO.getInfo())
                .image(UploadImage.uploadImage(requestDTO.getImage()))
                .showLink(requestDTO.getShowLink())
                .showLink2(requestDTO.getShowLink2())
                .marketLink(requestDTO.getMarketLink())
                .build();
    }

    public Movie masterpieceRequestDTOToMovie (MasterpieceRequestDTO requestDTO) throws IOException {
        return Movie.Builder.newBuilder()
                .title(requestDTO.getTitle())
                .owner(requestDTO.getOwner())
                .info(requestDTO.getInfo())
                .image(UploadImage.uploadImage(requestDTO.getImage()))
                .showLink(requestDTO.getShowLink())
                .showLink2(requestDTO.getShowLink2())
                .marketLink(requestDTO.getMarketLink())
                .build();
    }

    public MasterpieceOfTheWeekResponseDTO masterpieceToMasterpieceOfTheWeekResponseDTO(Masterpiece masterpiece){
        return MasterpieceOfTheWeekResponseDTO.builder()
                .id(masterpiece.getId())
                .title(masterpiece.getTitle())
                .owner(masterpiece.getOwner())
                .info(masterpiece.getInfo())
                .image(masterpiece.getImage().getId())
                .showLink(masterpiece.getShowLink())
                .showLink2(masterpiece.getShowLink2())
                .marketLink(masterpiece.getMarketLink())
                .createDateTime(masterpiece.getCreateDateTime())
                .updateDateTime(masterpiece.getUpdateDateTime())
                .build();
    }

    public MasterpieceResponseDTO masterpieceToMasterpieceResponseDTO(Masterpiece masterpiece){
        return MasterpieceResponseDTO.builder()
                .id(masterpiece.getId())
                .title(masterpiece.getTitle())
                .owner(masterpiece.getOwner())
                .info(masterpiece.getInfo())
                .image(masterpiece.getImage().getId())
                .showLink(masterpiece.getShowLink())
                .showLink2(masterpiece.getShowLink2())
                .marketLink(masterpiece.getMarketLink())
                .createDateTime(masterpiece.getCreateDateTime())
                .updateDateTime(masterpiece.getUpdateDateTime())
                .build();
    }
}
