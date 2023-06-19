package com.serbest.magazine.backend.dto.like;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeRequestDTO {
    private String postId;
    private String commentId;
}
