package com.wemingle.core.domain.member.dto;

import jakarta.validation.constraints.*;
import lombok.Value;

/**
 * DTO for {@link com.wemingle.core.domain.member.entity.Member}
 */
@Value
public class SignUpDto {
    @NotNull
    @NotEmpty
    @NotBlank
    String memberId;

    @NotNull
    @Size(min = 8, max = 20)
    @NotEmpty
    @NotBlank
    String password;
}