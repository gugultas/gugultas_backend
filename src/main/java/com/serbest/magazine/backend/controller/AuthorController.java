package com.serbest.magazine.backend.controller;


import com.serbest.magazine.backend.dto.author.AuthorCardResponseDTO;
import com.serbest.magazine.backend.dto.author.AuthorListResponseDTO;
import com.serbest.magazine.backend.dto.author.UserListResponseDTO;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.dto.user.AuthorResponseDTO;
import com.serbest.magazine.backend.dto.user.AuthorUpdateRequestDTO;
import com.serbest.magazine.backend.service.AuthorService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "https://magazine-app.netlify.app"}, maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserListResponseDTO>> getAllUsers(){
        return ResponseEntity.ok(authorService.getAllUsers());
    }

    @GetMapping("/getUsers")
    public ResponseEntity<List<AuthorResponseDTO>> getOnlyUsers(){
        return ResponseEntity.ok(authorService.getUsers());
    }

    @GetMapping("/getAuthors")
    public ResponseEntity<List<AuthorListResponseDTO>> getOnlyAuthors(){
        return ResponseEntity.ok(authorService.getAuthors());
    }

    @GetMapping("/getAuthorsForCard")
    public ResponseEntity<List<AuthorCardResponseDTO>> getAuthorsForCard(){
        return ResponseEntity.ok(authorService.getAuthorsForCard());
    }

    @GetMapping("/getUserByUsername/{username}")
    public ResponseEntity<AuthorResponseDTO> getAuthor(@PathVariable String username){
        return ResponseEntity.ok(authorService.getAuthorByUsername(username));
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @PutMapping(value = "/updateAuthor/{userId}",consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<AuthorResponseDTO> updateAuthor(@PathVariable String userId,
                                                          @ModelAttribute AuthorUpdateRequestDTO requestDTO
    ) throws IOException {
        return ResponseEntity.ok(authorService.updateUser(userId, requestDTO));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/makeAuthor/{userId}")
    public ResponseEntity<?> makeAuthor(@PathVariable String userId){
        return ResponseEntity.ok(authorService.makeAuthor(userId));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/unMakeAuthor/{userId}")
    public ResponseEntity<?> unMakeAuthor(@PathVariable String userId){
        return ResponseEntity.ok(authorService.unMakeAuthor(userId));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/makeEditor/{userId}")
    public ResponseEntity<?> makeEditor(@PathVariable String userId){
        return ResponseEntity.ok(authorService.makeEditor(userId));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/unMakeEditor/{userId}")
    public ResponseEntity<?> unMakeEditor(@PathVariable String userId){
        return ResponseEntity.ok(authorService.unMakeEditor(userId));
    }

    @PutMapping("/deactivateAuthor/{userId}")
    public ResponseEntity<AuthorResponseDTO> deactivateUser(@PathVariable String userId) throws AccessDeniedException {
        return ResponseEntity.ok(authorService.deactivateUser(userId));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/removeUser/{username}")
    public ResponseEntity<MessageResponseDTO> deleteUser(@PathVariable String username)  {
        return ResponseEntity.ok(authorService.deleteCompleteUser(username));
    }
}
