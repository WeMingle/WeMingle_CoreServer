package com.wemingle.core.domain.matching.vo;

import com.wemingle.core.domain.matching.dto.requesttitlestatus.RequestTitleStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TitleInfo{
    private String title;
    private RequestTitleStatus requestTitleStatus;
}