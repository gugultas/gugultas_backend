package com.serbest.magazine.backend.controller;

import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.dto.playlist.*;
import com.serbest.magazine.backend.service.PlaylistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "https://gugultas.com"}, maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<MessageResponseDTO> createPlaylist(@Valid @ModelAttribute PlaylistRequestDTO requestDTO) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(playlistService.createPlaylist(requestDTO));
    }

    @PutMapping(value = "/updatePlaylist/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<PlaylistResponseDTO>
        updatePlaylist(@PathVariable String id, @Valid @ModelAttribute PlaylistUpdateRequestDTO requestDTO) throws IOException {
        return ResponseEntity.ok(playlistService.updatePlaylist(id,requestDTO));
    }

    @GetMapping(value = "/allPlaylistByAuthor/{username}")
    public ResponseEntity<List<PlaylistResponseDTO>> allPlaylistByAuthor(@PathVariable String username){
        return ResponseEntity.ok(playlistService.allPlaylistByAuthor(username));
    }

    @GetMapping(value = "/getPlaylistById/{id}")
    public ResponseEntity<PlaylistResponseDTO> getPlaylistById(@PathVariable String id){
        return ResponseEntity.ok(playlistService.getPlaylistById(id));
    }

    @DeleteMapping(value = "/deletePlaylist/{id}")
    public ResponseEntity<MessageResponseDTO> deletePlaylistById(@PathVariable String id){
        return ResponseEntity.ok(playlistService.deletePlaylist(id));
    }

    @PutMapping("/addPostToPlaylist")
    public ResponseEntity<MessageResponseDTO> addPostToPlaylist(@Valid @RequestBody AddPostToPlaylistDTO playlistDTO) throws AccessDeniedException {
        return ResponseEntity.ok(playlistService.addPostToPlaylist(playlistDTO));
    }

    @PutMapping("/removePostFromPlaylist")
    public ResponseEntity<MessageResponseDTO>
        removePostFromPlaylist(@Valid @RequestBody RemovePostFromPlaylistDTO removePostFromPlaylistDTO) throws AccessDeniedException {
        return ResponseEntity.ok(playlistService.removePostFromPlaylist(removePostFromPlaylistDTO));
    }


}
