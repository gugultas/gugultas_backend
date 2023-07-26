package com.serbest.magazine.backend.mapper;

import com.serbest.magazine.backend.dto.playlist.PlaylistRequestDTO;
import com.serbest.magazine.backend.dto.playlist.PlaylistResponseDTO;
import com.serbest.magazine.backend.entity.Playlist;
import com.serbest.magazine.backend.util.UploadImage;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PlaylistMapper {

    public Playlist playlistRequestDTOToPlaylist(PlaylistRequestDTO requestDTO) throws IOException {
        return Playlist.Builder.newBuilder()
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .playlistImage(UploadImage.uploadImage(requestDTO.getPlaylistImage()))
                .build();
    }

    public PlaylistResponseDTO playlistToPlaylistResponseDTO(Playlist playlist){
        return PlaylistResponseDTO.builder()
                .id(playlist.getId())
                .title(playlist.getTitle())
                .description(playlist.getDescription())
                .playlistImage(playlist.getPlaylistImage().getId())
                .playlistImageType(playlist.getPlaylistImage().getType())
                .createDateTime(playlist.getCreateDateTime())
                .updateDateTime(playlist.getUpdateDateTime())
                .build();
    }
}
