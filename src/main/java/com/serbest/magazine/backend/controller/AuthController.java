package com.serbest.magazine.backend.controller;

import com.serbest.magazine.backend.dto.auth.*;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.mapper.UserMapper;
import com.serbest.magazine.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:3000", "https://gugultas.com"}, maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    public AuthController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }


    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<UserInfoResponse> login(@RequestBody LoginRequestDTO loginDto, HttpServletRequest request) {
        JWTAuthResponse jwtAuthResponse = authService.login(request, loginDto);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtAuthResponse.getRefreshTokenCookie().toString())
                .body(userMapper.jwtAuthResponseToUserInfoResponse(jwtAuthResponse));
    }

    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerDto){
        RegisterResponseDTO response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/confirmUser/{token}")
    public ResponseEntity<MessageResponseDTO> confirmUser(@PathVariable String token){
        return ResponseEntity.ok(authService.confirmActivation(token));
    }

    @CrossOrigin(maxAge = 640000)
    @PostMapping(value = "/sendActivationRequest")
    public ResponseEntity<MessageResponseDTO> sendActivationRequest(@Valid @RequestBody ActivationRequestDTO requestDTO){
        return ResponseEntity.ok(authService.sendActivation(requestDTO.getEmail()));
    }

    @PostMapping(value = "/forgotPassword")
    public ResponseEntity<MessageResponseDTO> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO requestDTO){
        return ResponseEntity.ok(authService.forgotPassword(requestDTO));
    }

    @PostMapping(value = "/changePassword/{token}")
    public ResponseEntity<MessageResponseDTO> changePassword(@PathVariable String token,  @Valid @RequestBody ResetPasswordRequestDTO requestDTO){
        return ResponseEntity.ok(authService.resetPassword(token, requestDTO));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(authService.refreshTokenHandle(request));
    }

    @PostMapping("/signout")
    public ResponseEntity<String> logoutUser() {
        ResponseCookie jwtRefreshCookie = authService.logout();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body("You've been signed out!");
    }
}
