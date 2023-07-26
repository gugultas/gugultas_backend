package com.serbest.magazine.backend.service;

import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.dto.playlist.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

public interface PlaylistService {

    MessageResponseDTO createPlaylist(PlaylistRequestDTO requestDTO) throws IOException;

    PlaylistResponseDTO updatePlaylist(String id, PlaylistUpdateRequestDTO requestDTO) throws IOException;

    List<PlaylistResponseDTO> allPlaylistByAuthor(String authorName);

    PlaylistResponseDTO getPlaylistById(String id);

    MessageResponseDTO deletePlaylist(String playlistID);

    MessageResponseDTO addPostToPlaylist(AddPostToPlaylistDTO addPostToPlaylistDTO) throws AccessDeniedException;

    MessageResponseDTO removePostFromPlaylist(RemovePostFromPlaylistDTO removePostFromPlaylistDTO) throws AccessDeniedException;
}
