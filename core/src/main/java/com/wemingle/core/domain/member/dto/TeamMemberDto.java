package com.wemingle.core.domain.member.dto;

import com.wemingle.core.domain.member.vo.MemberSummaryInfoVo;
import com.wemingle.core.domain.post.entity.gender.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

public class TeamMemberDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseTeamMembers{
        private String nickname;
        private String imgUrl;

        @Builder
        public ResponseTeamMembers(String nickname, String imgUrl) {
            this.nickname = nickname;
            this.imgUrl = imgUrl;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseTeamMemberInfo{
        private String imgUrl;
        private String nickname;
        private String introduction;
        private String univName;
        private Gender gender;
        private String ability;
        private String majorArea;
        private String age;
        private int matchingCnt;
        private int reportCnt;
        private LocalDate createdTime;

        @Builder
        public ResponseTeamMemberInfo(String imgUrl, String nickname, String introduction, MemberSummaryInfoVo memberSummaryInfoVo, int matchingCnt, LocalDate createdTime) {
            this.imgUrl = imgUrl;
            this.nickname = nickname;
            this.introduction = introduction;
            this.univName = memberSummaryInfoVo.getUnivName();
            this.gender = memberSummaryInfoVo.getGender();
            this.ability = memberSummaryInfoVo.getAbility();
            this.majorArea = memberSummaryInfoVo.getMajorArea();
            this.age = memberSummaryInfoVo.getAge();
            this.matchingCnt = matchingCnt;
            this.reportCnt = memberSummaryInfoVo.getReportCnt();
            this.createdTime = createdTime;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestTeamMemberProfileUpdate {
        private Long teamMemberPk;
        private String nickname;
    }
}
