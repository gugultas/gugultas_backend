package com.serbest.magazine.backend.controller;

import com.serbest.magazine.backend.common.dto.*;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "https://gugultas.com"}, maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MessageResponseDTO> createMusic(@Valid @ModelAttribute MasterpieceRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.create(requestDTO));
    }

    @GetMapping(value = "/getTopOfTheWeek")
    public ResponseEntity<MasterpieceOfTheWeekResponseDTO> getTopOfTheWeek() {
        return ResponseEntity.ok(movieService.getMasterpieceOfTheWeek());
    }

    @GetMapping(value = "/getMasterpieceById/{id}")
    public ResponseEntity<MasterpieceResponseDTO> getMasterpieceById(@PathVariable String id) {
        return ResponseEntity.ok(movieService.getMasterpieceById(id));
    }

    @GetMapping(value = "/getMasterpieces")
    public ResponseEntity<List<MasterpieceListResponseDTO>> getMasterpieces() {
        return ResponseEntity.ok(movieService.getMasterpieces());
    }

    @PutMapping(value = "/updateMasterpieceById/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MasterpieceResponseDTO> updateMasterpiece(@PathVariable String id, @Valid @ModelAttribute MasterpieceUpdateRequestDTO requestDTO) {
        return ResponseEntity.ok(movieService.updateMasterpiece(id, requestDTO));
    }


}
