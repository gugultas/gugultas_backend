package com.serbest.magazine.backend.service.impl;

import com.serbest.magazine.backend.common.validation.StringValidationCommon;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.dto.playlist.*;
import com.serbest.magazine.backend.entity.Author;
import com.serbest.magazine.backend.entity.Playlist;
import com.serbest.magazine.backend.entity.Post;
import com.serbest.magazine.backend.entity.Role;
import com.serbest.magazine.backend.exception.CustomApplicationException;
import com.serbest.magazine.backend.exception.ResourceNotFoundException;
import com.serbest.magazine.backend.mapper.PlaylistMapper;
import com.serbest.magazine.backend.repository.AuthorRepository;
import com.serbest.magazine.backend.repository.PlaylistRepository;
import com.serbest.magazine.backend.repository.PostRepository;
import com.serbest.magazine.backend.security.CheckAuthorization;
import com.serbest.magazine.backend.service.PlaylistService;
import com.serbest.magazine.backend.util.UploadImage;
import io.jsonwebtoken.lang.Assert;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final CheckAuthorization checkAuthorization;
    private final PlaylistRepository playlistRepository;
    private final PlaylistMapper playlistMapper;
    private final AuthorRepository authorRepository;
    private final PostRepository postRepository;

    public PlaylistServiceImpl(CheckAuthorization checkAuthorization, PlaylistRepository playlistRepository, PlaylistMapper playlistMapper, AuthorRepository authorRepository, PostRepository postRepository) {
        this.checkAuthorization = checkAuthorization;
        this.playlistRepository = playlistRepository;
        this.playlistMapper = playlistMapper;
        this.authorRepository = authorRepository;
        this.postRepository = postRepository;
    }

    @Override
    public MessageResponseDTO createPlaylist(PlaylistRequestDTO requestDTO) throws IOException {
        checkValidateAndSanitizeInput("title", requestDTO.getTitle());
        checkValidateAndSanitizeInput("content", requestDTO.getDescription());

        StringValidationCommon.common_validateStringLength(1, 75, requestDTO.getTitle());


        SecurityContext context = SecurityContextHolder.getContext();
        String usernameOrEmail = context.getAuthentication().getName();

        Author user = authorRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(
                () -> new ResourceNotFoundException("Author", "emailOrUsername", usernameOrEmail)
        );

        Playlist playlist = playlistMapper.playlistRequestDTOToPlaylist(requestDTO);
        playlist.setAuthor(user);

        Playlist newPlaylist = playlistRepository.save(playlist);

        return new MessageResponseDTO(newPlaylist.getTitle() +  " başlıklı yazı listeniz yaratılmıştır.");
    }

    @Override
    public PlaylistResponseDTO updatePlaylist(String id, PlaylistUpdateRequestDTO requestDTO) throws IOException {
        checkValidateAndSanitizeInput("id", id);
        checkValidateAndSanitizeInput("title", requestDTO.getTitle());
        checkValidateAndSanitizeInput("content", requestDTO.getDescription());

        StringValidationCommon.common_validateStringLength(1, 75, requestDTO.getTitle());

        SecurityContext context = SecurityContextHolder.getContext();
        String usernameOrEmail = context.getAuthentication().getName();

        Author user = authorRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(
                () -> new ResourceNotFoundException("Author", "emailOrUsername", usernameOrEmail)
        );

        Playlist playlist = playlistRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new ResourceNotFoundException("Playlist","id",id)
        );

        if (!playlist.getAuthor().getId().equals(user.getId())){
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Bunu yapmaya yetkiniz yoktur!");
        }

        playlist.setTitle(requestDTO.getTitle());
        playlist.setDescription(requestDTO.getDescription());
        if (!requestDTO.getImageProtect()) {
            playlist.setPlaylistImage(UploadImage.uploadImage(requestDTO.getPlaylistImage()));
        }

        return playlistMapper.playlistToPlaylistResponseDTO(playlistRepository.save(playlist));
    }

    @Override
    public List<PlaylistResponseDTO> allPlaylistByAuthor(String authorName) {
        return playlistRepository
                .findAllByAuthorUsernameOrderByCreateDateTimeDesc(authorName)
                .stream()
                .map(playlistMapper::playlistToPlaylistResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PlaylistResponseDTO getPlaylistById(String id) {
        Playlist playlist = playlistRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new ResourceNotFoundException("Playlist","id",id)
        );
        return playlistMapper.playlistToPlaylistResponseDTO(playlist);
    }

    @Override
    public MessageResponseDTO deletePlaylist(String playlistID) {
        checkValidateAndSanitizeInput("id", playlistID);

        SecurityContext context = SecurityContextHolder.getContext();
        String usernameOrEmail = context.getAuthentication().getName();

        Author user = authorRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(
                () -> new ResourceNotFoundException("Author", "emailOrUsername", usernameOrEmail)
        );

        Playlist playlist = playlistRepository.findById(UUID.fromString(playlistID)).orElseThrow(
                () -> new ResourceNotFoundException("Playlist","id",playlistID)
        );

        if (!playlist.getAuthor().getId().equals(user.getId())){
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Bunu yapmaya yetkiniz yoktur!");
        }

        playlistRepository.deleteById(UUID.fromString(playlistID));

        return new MessageResponseDTO(playlistID + " ID'li playlist başarıyla silinmiştir.");
    }

    @Override
    public MessageResponseDTO addPostToPlaylist(AddPostToPlaylistDTO addPostToPlaylistDTO) throws AccessDeniedException {
        checkValidateAndSanitizeInput("playlistID", addPostToPlaylistDTO.getPlaylistId());
        checkValidateAndSanitizeInput("postID", addPostToPlaylistDTO.getPostId());

        Playlist playlist = getPlaylist(addPostToPlaylistDTO.getPlaylistId());

        Post post = postRepository.findById(UUID.fromString(addPostToPlaylistDTO.getPostId())).orElseThrow(
                () -> new ResourceNotFoundException("Post","id",addPostToPlaylistDTO.getPostId())
        );

        if (!playlist.getAuthor().getId().equals(post.getAuthor().getId())){
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST,"Sadece kendinize ait yazıları ekleyebilirsiniz.");
        }

        Set<Post> posts = playlist.getPosts();

        if (!posts.contains(post)){
            posts.add(post);
        }

        playlist.setPosts(posts);

        playlistRepository.save(playlist);

        return new MessageResponseDTO( post.getTitle() + " başlıklı post başarıyla oynatma listesine eklendi.");
    }

    @Override
    public MessageResponseDTO removePostFromPlaylist(RemovePostFromPlaylistDTO removePostFromPlaylistDTO) throws AccessDeniedException {
        checkValidateAndSanitizeInput("playlistID", removePostFromPlaylistDTO.getPlaylistId());
        checkValidateAndSanitizeInput("potID", removePostFromPlaylistDTO.getPostId());

        Playlist playlist = getPlaylist(removePostFromPlaylistDTO.getPlaylistId());

        Post post = postRepository.findById(UUID.fromString(removePostFromPlaylistDTO.getPostId())).orElseThrow(
                () -> new ResourceNotFoundException("Post","id",removePostFromPlaylistDTO.getPostId())
        );

        Set<Post> posts = playlist.getPosts();

        if (posts.contains(post)){
            posts.remove(post);
        }

        playlist.setPosts(posts);

        playlistRepository.save(playlist);

        return new MessageResponseDTO( post.getTitle() + " başlıklı post başarıyla oynatma listesinden kaldırıldı.");
    }

    private Playlist getPlaylist(String id) throws AccessDeniedException {
        Playlist playlist = playlistRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new ResourceNotFoundException("Playlist", "id", id)
        );
        checkAuthorization.checkUser(playlist.getAuthor());

        return playlist;
    }

    private void checkValidateAndSanitizeInput(String fieldName, String fieldValue) {
        Assert.notNull(fieldValue, "Please , provide a valid " + fieldName + ".");
        if (fieldValue.isEmpty() || fieldValue.isBlank()) {
            throw new IllegalArgumentException("Please , provide a valid " + fieldName + ".");
        }
    }
}
