package com.wemingle.core.domain.matching.dto;

import com.wemingle.core.domain.matching.dto.requesttitlestatus.RequestTitleStatus;
import com.wemingle.core.domain.matching.vo.TitleInfo;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

public class MatchingRequestDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseMatchingRequestHistory {
        private String title;
        private RequestTitleStatus requestTitleStatus;
        private String content;
        private LocalDate requestDate;
        private MatchingStatus matchingStatus;
        private Long matchingRequestPk;
        private Long matchingPostPk;

        @Builder
        public ResponseMatchingRequestHistory(TitleInfo titleInfo, String content, LocalDate requestDate, MatchingStatus matchingStatus, Long matchingRequestPk, Long matchingPostPk) {
            this.title = titleInfo.getTitle();
            this.requestTitleStatus = titleInfo.getRequestTitleStatus();
            this.content = content;
            this.requestDate = requestDate;
            this.matchingStatus = matchingStatus;
            this.matchingRequestPk = matchingRequestPk;
            this.matchingPostPk = matchingPostPk;
        }
    }
}
