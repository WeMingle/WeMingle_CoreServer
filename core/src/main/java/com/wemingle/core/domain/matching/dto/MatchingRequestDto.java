package com.wemingle.core.domain.matching.dto;

import com.wemingle.core.domain.matching.dto.requesttitlestatus.RequestTitleStatus;
import com.wemingle.core.domain.matching.vo.TitleInfo;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

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

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponsePendingRequestsByIndividual{
        private String title;
        private LinkedHashMap<Long, RequestInfoByIndividual> requestsInfo;

        @Builder
        public ResponsePendingRequestsByIndividual(String title, LinkedHashMap<Long, RequestInfoByIndividual> requestsInfo) {
            this.title = title;
            this.requestsInfo = requestsInfo;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestInfoByIndividual {
        private String profileImg;
        private String nickname;
        private String content;
        private int completedMatchingCnt;
        private String majorActivityArea;
        private Ability ability;

        @Builder
        public RequestInfoByIndividual(String profileImg, String nickname, String content, int completedMatchingCnt, String majorActivityArea, Ability ability) {
            this.profileImg = profileImg;
            this.nickname = nickname;
            this.content = content;
            this.completedMatchingCnt = completedMatchingCnt;
            this.majorActivityArea = majorActivityArea;
            this.ability = ability;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponsePendingRequestsByTeam{
        private String title;
        LinkedHashMap<Long, RequestInfoByTeam> requestsInfo;

        @Builder
        public ResponsePendingRequestsByTeam(String title, LinkedHashMap<Long, RequestInfoByTeam> requestsInfo) {
            this.title = title;
            this.requestsInfo = requestsInfo;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestInfoByTeam {
        private String profileImg;
        private String teamName;
        private String content;
        private int completedMatchingCnt;
        private int teamMemberCnt;
        private double teamRating;

        @Builder
        public RequestInfoByTeam(String profileImg, String teamName, String content, int completedMatchingCnt, int teamMemberCnt, double teamRating) {
            this.profileImg = profileImg;
            this.teamName = teamName;
            this.content = content;
            this.completedMatchingCnt = completedMatchingCnt;
            this.teamMemberCnt = teamMemberCnt;
            this.teamRating = teamRating;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchingRequestApprove {
        List<Long> matchingRequests;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchingRequestDelete {
        List<Long> matchingRequests;
    }
}
