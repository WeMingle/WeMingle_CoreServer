package com.wemingle.core.domain.member.service;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.vo.SignupVo;

public interface MemberService {
    boolean verifyAvailableId(String memberId);
    void saveMember(SignupVo.SaveMemberVo saveMemberVo);
    void patchMemberProfile(SignupVo.PatchMemberProfileVo patchMemberProfileVo);
    Member findByRefreshToken(String refreshToken);
    Member findByMemberId(String memberId);
}