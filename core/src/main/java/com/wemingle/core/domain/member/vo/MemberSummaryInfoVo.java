package com.wemingle.core.domain.member.vo;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.gender.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSummaryInfoVo {
    private String univName;
    private Gender gender;
    private String ability;
    private String majorArea;
    private String age;
    private int reportCnt;

    @Builder
    public MemberSummaryInfoVo(String univName, Member member, String findAbility) {
        final String IS_NOT_PUBLIC = "비공개";
        this.univName = univName;
        this.gender = member.getGender();
        this.ability = member.isAbilityPublic() ? findAbility : IS_NOT_PUBLIC;
        this.majorArea = member.isMajorActivityAreaPublic() ? member.getMajorActivityArea().toString() : IS_NOT_PUBLIC;
        this.age = member.isBirthYearPublic() ? String.valueOf(member.getBirthYear()) : IS_NOT_PUBLIC;;
        this.reportCnt = member.getComplaintsCount();
    }
}
