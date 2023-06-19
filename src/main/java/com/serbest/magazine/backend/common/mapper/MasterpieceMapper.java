package com.serbest.magazine.backend.common.mapper;

import com.serbest.magazine.backend.common.dto.MasterpieceOfTheWeekResponseDTO;
import com.serbest.magazine.backend.common.dto.MasterpieceRequestDTO;
import com.serbest.magazine.backend.common.dto.MasterpieceResponseDTO;
import com.serbest.magazine.backend.common.entity.Masterpiece;
import com.serbest.magazine.backend.entity.Movie;
import com.serbest.magazine.backend.entity.Music;
import com.serbest.magazine.backend.entity.Picture;
import org.springframework.stereotype.Component;

@Component
public class MasterpieceMapper {

    public Music masterpieceRequestDTOToMusic (MasterpieceRequestDTO requestDTO) {
        return Music.Builder.newBuilder()
                .title(requestDTO.getTitle())
                .owner(requestDTO.getOwner())
                .info(requestDTO.getInfo())
                .showLink(requestDTO.getShowLink())
                .showLink2(requestDTO.getShowLink2())
                .marketLink(requestDTO.getMarketLink())
                .build();
    }

    public Picture masterpieceRequestDTOToPicture (MasterpieceRequestDTO requestDTO) {
        return Picture.Builder.newBuilder()
                .title(requestDTO.getTitle())
                .owner(requestDTO.getOwner())
                .info(requestDTO.getInfo())
                .showLink(requestDTO.getShowLink())
                .showLink2(requestDTO.getShowLink2())
                .marketLink(requestDTO.getMarketLink())
                .build();
    }

    public Movie masterpieceRequestDTOToMovie (MasterpieceRequestDTO requestDTO) {
        return Movie.Builder.newBuilder()
                .title(requestDTO.getTitle())
                .owner(requestDTO.getOwner())
                .info(requestDTO.getInfo())
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
                .image(masterpiece.getImage())
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
                .image(masterpiece.getImage())
                .showLink(masterpiece.getShowLink())
                .showLink2(masterpiece.getShowLink2())
                .marketLink(masterpiece.getMarketLink())
                .createDateTime(masterpiece.getCreateDateTime())
                .updateDateTime(masterpiece.getUpdateDateTime())
                .build();
    }
}
