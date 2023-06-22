package com.serbest.magazine.backend.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirstFivePostsListDTO {
    private UUID id;
    private UUID image;
    private String title;
    private UUID avatarId;
    private String avatarType;
}
