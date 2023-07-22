package com.serbest.magazine.backend.controller;

import com.serbest.magazine.backend.dto.encyclopediaArticle.EncyclopediaArticleRequestDTO;
import com.serbest.magazine.backend.dto.encyclopediaArticle.EncyclopediaArticleResponseDTO;
import com.serbest.magazine.backend.dto.encyclopediaArticle.EncyclopediaArticleUpdateRequestDTO;
import com.serbest.magazine.backend.dto.encyclopediaArticle.EncyclopediaArticleUpdateResponseDTO;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.service.EncyclopediaArticleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "https://gugultas.com"}, maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/encyclopediaArticles")
public class EncyclopediaArticleController {

    private final EncyclopediaArticleService encyclopediaArticleService;

    public EncyclopediaArticleController(EncyclopediaArticleService encyclopediaArticleService) {
        this.encyclopediaArticleService = encyclopediaArticleService;
    }

    @PostMapping
    public ResponseEntity<MessageResponseDTO>
        createEncyclopediaArticle(@Valid @RequestBody EncyclopediaArticleRequestDTO requestDTO){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(encyclopediaArticleService.createEncyclopediaArticle(requestDTO));
    }

    @PutMapping("/updateEncyclopediaArticle/{id}")
    public ResponseEntity<EncyclopediaArticleUpdateResponseDTO> updateEncyclopediaArticle(
                    @PathVariable String id, @Valid @RequestBody EncyclopediaArticleUpdateRequestDTO updateRequestDTO
    ){
        return ResponseEntity.ok(encyclopediaArticleService.updateEncyclopediaArticle(id,updateRequestDTO));
    }

    @DeleteMapping("/deleteEncyclopediaArticle/{id}")
    public ResponseEntity<MessageResponseDTO> deleteEncyclopediaArticle(@PathVariable String id){
        return ResponseEntity.ok(encyclopediaArticleService.deleteEncyclopediaArticle(id));
    }

    @GetMapping
    public ResponseEntity<List<EncyclopediaArticleResponseDTO>> getAllEncyclopediaArticles(){
        return ResponseEntity.ok(encyclopediaArticleService.getAllEncyclopediaArticles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EncyclopediaArticleResponseDTO> getEncyclopediaArticleById(@PathVariable String id){
        return ResponseEntity.ok(encyclopediaArticleService.getEncyclopediaArticleById(id));
    }

    @GetMapping("/getLastSevenEncyclopediaArticle")
    public ResponseEntity<List<EncyclopediaArticleResponseDTO>> getLastSevenEncyclopediaArticle(){
        return ResponseEntity.ok(encyclopediaArticleService.getLastSevenEncyclopediaArticle());
    }
}
