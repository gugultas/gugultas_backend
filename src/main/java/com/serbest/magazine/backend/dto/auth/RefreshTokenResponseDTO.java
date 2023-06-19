package com.serbest.magazine.backend.dto.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenResponseDTO {
    private String accessToken;
    private String username;
    private String image;
    private List<String> roles;
    private String message;
}
