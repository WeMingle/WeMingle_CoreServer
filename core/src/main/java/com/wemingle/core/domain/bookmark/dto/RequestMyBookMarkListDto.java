package com.wemingle.core.domain.bookmark.dto;

import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestMyBookMarkListDto {
    private Long nextIdx;
    private boolean excludeExpired;
    private RecruiterType recruiterType;
}
