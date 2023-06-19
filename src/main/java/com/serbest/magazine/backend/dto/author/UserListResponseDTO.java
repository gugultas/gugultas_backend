package com.serbest.magazine.backend.dto.author;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListResponseDTO {
    private UUID id;
    private String username;
    private List<String> roles;
}
