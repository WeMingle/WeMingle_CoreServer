package com.wemingle.core.domain.member.service;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.member.dto.MemberAuthenticationInfoDto;
import com.wemingle.core.domain.member.dto.MemberInfoDto;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import com.wemingle.core.domain.member.vo.SignupVo;

import java.util.List;

public interface MemberService {
    boolean verifyAvailableId(String memberId);
    void saveMember(SignupVo.SaveMemberVo saveMemberVo);
    void patchMemberProfile(SignupVo.PatchMemberProfileVo patchMemberProfileVo);
    Member findByRefreshToken(String refreshToken);
    Member findByMemberId(String memberId);
    boolean isRegisteredMember(String memberId, SignupPlatform platform);
    void saveMemberPreferenceSports(String memberId, List<SportsType> preferenceSports);

    MemberInfoDto getMemberInfo(String memberId);
    void setMemberInfo(String memberId, MemberInfoDto memberInfoDto);
    MemberAuthenticationInfoDto getMemberAuthenticationInfo(String memberId);
}
