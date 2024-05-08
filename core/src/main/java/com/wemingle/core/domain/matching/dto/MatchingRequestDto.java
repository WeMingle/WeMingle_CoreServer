package com.wemingle.core.domain.matching.dto;

import com.wemingle.core.domain.matching.dto.requesttitlestatus.RequestTitleStatus;
import com.wemingle.core.domain.matching.entity.MatchingRequest;
import com.wemingle.core.domain.matching.entity.requestmembertype.RequestMemberType;
import com.wemingle.core.domain.matching.vo.IsExceedCapacityLimitVo;
import com.wemingle.core.domain.matching.vo.TitleInfo;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
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
        private AreaName majorActivityArea;
        private Ability ability;

        @Builder
        public RequestInfoByIndividual(String profileImg, String nickname, String content, int completedMatchingCnt, AreaName majorActivityArea, Ability ability) {
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
        private List<Long> matchingRequests;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchingRequestDelete {
        private List<Long> matchingRequests;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestMatchingRequestSave {
        private Long matchingPostPk;
        private Long requestTeamPk;
        private List<Long> participantsPk = new ArrayList<>(); // member pk
        private int capacityCnt;
        @NotBlank
        private String content;

        public IsExceedCapacityLimitVo of(){
            return IsExceedCapacityLimitVo.builder()
                    .matchingPostPk(matchingPostPk)
                    .capacityCnt(capacityCnt)
                    .build();
        }

        public MatchingRequest of(Team requestTeam, Member requester, MatchingPost matchingPost){
            MatchingStatus matchingStatus = matchingPost.getRecruitmentType().equals(RecruitmentType.APPROVAL_BASED)
                    ? MatchingStatus.PENDING
                    : MatchingStatus.COMPLETE;

            return MatchingRequest.builder()
                    .content(content)
                    .capacityCnt(capacityCnt)
                    .team(requestTeam)
                    .matchingRequestStatus(matchingStatus)
                    .requestMemberType(RequestMemberType.REQUESTER)
                    .member(requester)
                    .matchingPost(matchingPost)
                    .build();
        }

        public List<MatchingRequest> of(Team requestTeam, List<Member> requesters, MatchingPost matchingPost){
            MatchingStatus matchingStatus = matchingPost.getRecruitmentType().equals(RecruitmentType.APPROVAL_BASED)
                    ? MatchingStatus.PENDING
                    : MatchingStatus.COMPLETE;

            return requesters.stream().map(requester -> MatchingRequest.builder()
                    .content(content)
                    .capacityCnt(capacityCnt)
                    .requestMemberType(RequestMemberType.PARTICIPANT)
                    .matchingRequestStatus(matchingStatus)
                    .team(requestTeam)
                    .member(requester)
                    .matchingPost(matchingPost)
                    .build())
                    .toList();
        }
    }
}
