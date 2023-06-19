package com.serbest.magazine.backend.service;

import com.serbest.magazine.backend.dto.auth.*;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

import java.io.IOException;

public interface AuthService {

    RegisterResponseDTO register(RegisterRequestDTO requestDTO);

    MessageResponseDTO confirmActivation(String token);

    MessageResponseDTO sendActivation(String email);

    JWTAuthResponse login(HttpServletRequest request, LoginRequestDTO loginDto);

    MessageResponseDTO forgotPassword(ForgotPasswordRequestDTO requestDTO);

    MessageResponseDTO resetPassword(String token, ResetPasswordRequestDTO requestDTO);

    RefreshTokenResponseDTO refreshTokenHandle(HttpServletRequest request);

    ResponseCookie logout();
}
