package com.wemingle.core.domain.user.dto;

import jakarta.validation.constraints.*;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.wemingle.core.domain.user.entity.Member}
 */
@Value
public class SignUpDto {
    @NotNull
    @NotEmpty
    @NotBlank
    String memberName;

    @NotNull
    @NotEmpty
    @NotBlank
    String nickname;

    @NotNull
    String phoneNumber;

    @NotNull
    @PastOrPresent
    LocalDate dateOfBirth;

    @NotNull
    @Email
    @NotEmpty
    @NotBlank
    String email;

    @NotNull
    @Size
    @NotEmpty
    @NotBlank
    String password;
}