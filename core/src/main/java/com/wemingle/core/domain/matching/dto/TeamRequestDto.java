package com.wemingle.core.domain.matching.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.entity.teamrole.TeamRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class TeamRequestDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseRequesterInfo{
        private String imgUrl;
        private int matchingCnt;
        private String nickname;
        private String univName;
        private Gender gender;
        private String ability;
        private String majorArea;
        private String age;
        private int reportCnt;
        private HashMap<Long, String> teamQuestionnaires;

        @Builder
        public ResponseRequesterInfo(String imgUrl, int matchingCnt, String nickname, String univName, Gender gender, String ability, String majorArea, String age, int reportCnt, HashMap<Long, String> teamQuestionnaires) {
            this.imgUrl = imgUrl;
            this.matchingCnt = matchingCnt;
            this.nickname = nickname;
            this.univName = univName;
            this.gender = gender;
            this.ability = ability;
            this.majorArea = majorArea;
            this.age = age;
            this.reportCnt = reportCnt;
            this.teamQuestionnaires = teamQuestionnaires;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestTeamRequestSave{
        private Long teamPk;
        @NotBlank
        private String nickname;
        private HashMap<Long, String> answers;

        public TeamMember of(Member member, Team team){
            return TeamMember.builder()
                    .nickname(nickname)
                    .profileImg(member.getProfileImgId())
                    .teamRole(TeamRole.PARTICIPANT)
                    .member(member)
                    .team(team)
                    .build();
        }
    }
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseTeamRequests {
        private int remainCapacity;
        private HashMap<Long, RequesterSummary> requesterSummaries;

        @Builder
        public ResponseTeamRequests(int remainCapacity, HashMap<Long, RequesterSummary> requesterSummaries) {
            this.remainCapacity = remainCapacity;
            this.requesterSummaries = requesterSummaries;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequesterSummary {
        private String nickname;
        private String imgUrl;
        private int matchingCnt;
        private LocalDateTime createdTime;
        //todo 채팅방을 위한 데이터 추후에 추가

        @Builder
        public RequesterSummary(String nickname, String imgUrl, int matchingCnt, LocalDateTime createdTime) {
            this.nickname = nickname;
            this.imgUrl = imgUrl;
            this.matchingCnt = matchingCnt;
            this.createdTime = createdTime;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestTeamRequestDelete {
        private List<Long> teamRequestPk;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestTeamRequestApprove {
        private Long teamPk;
        private List<Long> teamRequestPk;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseTeamRequestInfo{
        private String imgUrl;
        private String nickname;
        private String univName;
        private Gender gender;
        private String ability;
        private String majorArea;
        private String age;
        private LocalDate createdDate;
        private int matchingCnt;
        private int reportCnt;
        @JsonProperty(value = "isApprovable")
        private boolean isApprovable;
        private HashMap<String, String> teamQuestionnaires;

        @Builder
        public ResponseTeamRequestInfo(String imgUrl, String nickname, String univName, Gender gender, String ability, String majorArea, String age, LocalDate createdDate, int matchingCnt, int reportCnt, boolean isApprovable, HashMap<String, String> teamQuestionnaires) {
            this.imgUrl = imgUrl;
            this.nickname = nickname;
            this.univName = univName;
            this.gender = gender;
            this.ability = ability;
            this.majorArea = majorArea;
            this.age = age;
            this.createdDate = createdDate;
            this.matchingCnt = matchingCnt;
            this.reportCnt = reportCnt;
            this.isApprovable = isApprovable;
            this.teamQuestionnaires = teamQuestionnaires;
        }
    }
}
