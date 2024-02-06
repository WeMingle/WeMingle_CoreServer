package com.wemingle.core.domain.member.dto;

import jakarta.validation.constraints.*;
import lombok.Value;

import java.time.LocalDate;

/**
 * DTO for {@link com.wemingle.core.domain.member.entity.Member}
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