package com.wemingle.core.domain.member.dto;

import com.wemingle.core.global.annotation.Essential;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDto {

    @Essential
    @Size(min = 8, max = 20)
    private String previousPassword;
    @Essential
    @Size(min = 8, max = 20)
    private String newPassword;
}
