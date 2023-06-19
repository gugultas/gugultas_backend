package com.serbest.magazine.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDTO {

    @NotBlank(message = "Provide a valid password , please.")
    @Size(min = 8, message = "Password cannot be less than 8 characters.")
    private String password;
}
