package com.wemingle.core.domain.member.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class SetMemberProfileDto {
    MultipartFile profilePic;
    String nickname;
}
