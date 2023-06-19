package com.serbest.magazine.backend.security.services;

import com.serbest.magazine.backend.entity.Author;
import com.serbest.magazine.backend.entity.RefreshToken;
import com.serbest.magazine.backend.exception.ResourceNotFoundException;
import com.serbest.magazine.backend.exception.TokenRefreshException;
import com.serbest.magazine.backend.repository.AuthorRepository;
import com.serbest.magazine.backend.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${magazine.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthorRepository authorRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, AuthorRepository authorRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.authorRepository = authorRepository;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(UUID authorId) {


        Author author = authorRepository.findById(authorId).orElseThrow(
                () -> new ResourceNotFoundException("Author", "Author Id", authorId.toString())
        );

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAuthor(author);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByUsername(String username) {
        Author author = authorRepository.findByUsernameOrEmail(username, username).orElseThrow(
                () -> new ResourceNotFoundException("Author", "Username or Email", username)
        );
        return refreshTokenRepository.deleteByAuthor(author);
    }
}
