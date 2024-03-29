package com.serbest.magazine.backend.service.impl;

import com.serbest.magazine.backend.common.dto.*;
import com.serbest.magazine.backend.common.mapper.MasterpieceMapper;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.entity.Movie;
import com.serbest.magazine.backend.exception.CustomApplicationException;
import com.serbest.magazine.backend.exception.ResourceNotFoundException;
import com.serbest.magazine.backend.repository.MovieRepository;
import com.serbest.magazine.backend.service.ImageModelService;
import com.serbest.magazine.backend.service.MovieService;
import com.serbest.magazine.backend.util.UploadImage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MasterpieceMapper masterpieceMapper;
    private final ImageModelService imageModelService;

    public MovieServiceImpl(MovieRepository movieRepository, MasterpieceMapper masterpieceMapper, ImageModelService imageModelService) {
        this.movieRepository = movieRepository;
        this.masterpieceMapper = masterpieceMapper;
        this.imageModelService = imageModelService;
    }

    @Override
    public MessageResponseDTO create(MasterpieceRequestDTO requestDTO) {
        Movie movie = null;
        try {
            movie = masterpieceMapper.masterpieceRequestDTOToMovie(requestDTO);

            Movie savedMovie = movieRepository.save(movie);

            return new MessageResponseDTO(savedMovie.getTitle() + " adlı film başarıyla eklenmiştir.");

        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public MasterpieceOfTheWeekResponseDTO getMasterpieceOfTheWeek() {
        return masterpieceMapper
                .masterpieceToMasterpieceOfTheWeekResponseDTO(movieRepository.findTopByOrderByCreateDateTimeDesc());
    }

    @Override
    public MasterpieceResponseDTO getMasterpieceById(String id) {
        Movie movie = movieRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new ResourceNotFoundException("Movie", "id", id)
        );
        return masterpieceMapper
                .masterpieceToMasterpieceResponseDTO(movie);
    }

    @Override
    public List<MasterpieceListResponseDTO> getMasterpieces() {
        return movieRepository
                .findAllByOrderByCreateDateTimeDesc()
                .stream()
                .map(masterpieceMapper::masterpieceToMasterpieceListResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MasterpieceResponseDTO updateMasterpiece(String id, MasterpieceUpdateRequestDTO requestDTO) {
        Movie movie = movieRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new ResourceNotFoundException("Movie", "id", id)
        );

        try {
            if (!requestDTO.getImageProtect()) {
                movie.setImage(UploadImage.uploadImage(requestDTO.getImage()));
            }
            movie.setTitle(requestDTO.getTitle());
            movie.setOwner(requestDTO.getOwner());
            movie.setInfo(requestDTO.getInfo());
            movie.setShowLink(requestDTO.getShowLink());
            movie.setShowLink2(requestDTO.getShowLink2());
            movie.setMarketLink(requestDTO.getMarketLink());

            return masterpieceMapper.masterpieceToMasterpieceResponseDTO(movieRepository.save(movie));

        } catch (Exception e) {
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
