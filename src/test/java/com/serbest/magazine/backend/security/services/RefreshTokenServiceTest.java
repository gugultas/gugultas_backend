package com.serbest.magazine.backend.security.services;

import com.serbest.magazine.backend.entity.RefreshToken;
import com.serbest.magazine.backend.repository.AuthorRepository;
import com.serbest.magazine.backend.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @InjectMocks
    RefreshTokenService refreshTokenService;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    AuthorRepository authorRepository;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 180000000L);
    }

    @Test
    public void test_findByToken_withSuccess() {
        RefreshToken refreshToken = mock(RefreshToken.class);

        refreshToken.setToken("fake token");
        when(refreshTokenRepository.findByToken("fake token")).thenReturn(Optional.of(refreshToken));

        Optional<RefreshToken> refreshToken1 = refreshTokenService.findByToken("fake token");

        assertEquals(refreshToken, refreshToken1.get());
        assertEquals(refreshToken.getToken(), refreshToken1.get().getToken());
        System.out.println("Workflow added");
    }

}