package com.wemingle.core.domain.member.service;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.member.dto.MemberAuthenticationInfoDto;
import com.wemingle.core.domain.member.dto.MemberDto;
import com.wemingle.core.domain.member.dto.MemberInfoDto;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import com.wemingle.core.domain.member.vo.SignupVo;

import java.util.HashMap;

public interface MemberService {
    boolean verifyAvailableId(String memberId);
    void saveMember(SignupVo.SaveMemberVo saveMemberVo);
    void patchMemberProfile(SignupVo.PatchMemberProfileVo patchMemberProfileVo);
    Member findByRefreshToken(String refreshToken);
    Member findByMemberId(String memberId);
    boolean isRegisteredMember(String memberId);
    SignupPlatform findRegisteredPlatformByMember(String memberId);
    boolean isMatchesPassword(String memberId, String rawPw);
    void saveMemberPreferenceSports(String memberId, SportsType preferenceSport);

    MemberInfoDto getMemberInfo(String memberId);
    void setMemberInfo(String memberId, MemberInfoDto memberInfoDto);
    MemberAuthenticationInfoDto getMemberAuthenticationInfo(String memberId);
    HashMap<Long, MemberDto.ResponseMemberInfoInSearch> getMemberByNickname(Long nextIdx, String nickname, String memberId);
    void patchMemberPassword(String memberId, String newPassword);
    Member findWithdrawMember();
}
