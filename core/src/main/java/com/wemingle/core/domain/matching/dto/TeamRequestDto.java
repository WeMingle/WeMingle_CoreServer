package com.wemingle.core.domain.matching.dto;

import com.wemingle.core.domain.post.entity.gender.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        private List<String> teamQuestionnaires;

        @Builder
        public ResponseRequesterInfo(String imgUrl, int matchingCnt, String nickname, String univName, Gender gender, String ability, String majorArea, String age, int reportCnt, List<String> teamQuestionnaires) {
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
}
