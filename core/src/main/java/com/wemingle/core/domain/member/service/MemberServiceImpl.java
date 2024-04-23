package com.wemingle.core.domain.member.service;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.member.dto.MemberAuthenticationInfoDto;
import com.wemingle.core.domain.member.dto.MemberDto;
import com.wemingle.core.domain.member.dto.MemberInfoDto;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.MemberAbility;
import com.wemingle.core.domain.member.entity.MemberPreferenceSports;
import com.wemingle.core.domain.member.entity.PolicyTerms;
import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import com.wemingle.core.domain.member.repository.MemberAbilityRepository;
import com.wemingle.core.domain.member.repository.MemberPreferenceSportsRepository;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.member.repository.PolicyTermsRepository;
import com.wemingle.core.domain.member.vo.SignupVo;
import com.wemingle.core.domain.memberunivemail.entity.VerifiedUniversityEmail;
import com.wemingle.core.domain.memberunivemail.repository.VerifiedUniversityEmailRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.wemingle.core.global.exceptionmessage.ExceptionMessage.MEMBER_NOT_FOUNT;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PolicyTermsRepository policyTermsRepository;
    private final MemberPreferenceSportsRepository memberPreferenceSportsRepository;
    private final VerifiedUniversityEmailRepository verifiedUniversityEmailRepository;
    private final S3ImgService s3ImgService;
    private final MemberAbilityRepository memberAbilityRepository;
    private final BCryptPasswordEncoder pwEncoder;

    @Override
    public boolean verifyAvailableId(String memberId) {
        return memberRepository.findByMemberId(memberId).isEmpty();
    }

    @Override
    @Transactional
    public void saveMember(SignupVo.SaveMemberVo saveMemberVo) {
        PolicyTerms policyTerms = savePolicyTerms(saveMemberVo.isAgreeToLocationBasedServices(), saveMemberVo.isAgreeToReceiveMarketingInformation());
        saveMemberVo.patchPassword(bCryptPasswordEncoder.encode(saveMemberVo.getPassword()));
        Member member = saveMemberVo.of(saveMemberVo);
        member.patchPolicyTerms(policyTerms);

        memberRepository.save(member);
    }

    private PolicyTerms savePolicyTerms(boolean agreeToLocationBasedServices, boolean agreeToReceiveMarketingInformation){
        return policyTermsRepository.save(PolicyTerms.builder()
                .agreeToLocationBasedServices(agreeToLocationBasedServices)
                .agreeToReceiveMarketingInformation(agreeToReceiveMarketingInformation)
                .build());
    }

    @Override
    @Transactional
    public void patchMemberProfile(SignupVo.PatchMemberProfileVo patchMemberProfileVo) {
        Member findMember = findByMemberId(patchMemberProfileVo.getMemberId());
        String nickname = patchMemberProfileVo.getNickname();

        findMember.patchMemberProfile(nickname);
    }

    @Override
    public Member findByRefreshToken(String refreshToken) {
        return memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUNT.getExceptionMessage()));
    }

    @Override
    public Member findByMemberId(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUNT.getExceptionMessage()));
    }

    @Override
    public boolean isRegisteredMember(String memberId, SignupPlatform platform) {
        Optional<Member> byMemberId = memberRepository.findByMemberId(memberId);
        return byMemberId.isPresent()&&byMemberId.get().getSignupPlatform().getPlatformType().equals(platform.getPlatformType());
    }

    @Override
    public SignupPlatform findRegisteredPlatformByMember(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new RuntimeException(MEMBER_NOT_FOUNT.getExceptionMessage()));
        return member.getSignupPlatform();
    }

    @Override
    public boolean isMatchesPassword(String memberId, String rawPw) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUNT.getExceptionMessage()));
        return pwEncoder.matches(rawPw, member.getPassword());
    }

    @Override
    @Transactional
    public void saveMemberPreferenceSports(String memberId, List<SportsType> preferenceSports) {
        Member findMember = findByMemberId(memberId);

        List<MemberPreferenceSports> memberPreferenceSportsList = preferenceSports.stream()
                .map(preferenceSportsCategory -> MemberPreferenceSports.builder()
                        .member(findMember)
                        .sports(preferenceSportsCategory)
                        .build())
                .toList();

        memberPreferenceSportsRepository.saveAll(memberPreferenceSportsList);
    }

    @Override
    public MemberInfoDto getMemberInfo(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUNT.getExceptionMessage()));
        List<MemberInfoDto.EachAbilityAboutMember> abilityAboutMembers = memberAbilityRepository.findMemberAbilitiesByMember(member).stream().map(memberAbility -> MemberInfoDto.EachAbilityAboutMember.builder().ability(memberAbility.getAbility()).sportsType(memberAbility.getSportsType()).build()).toList();
        return MemberInfoDto.builder().oneLineIntroduction(member.getOneLineIntroduction())
                .nickname(member.getNickname())
                .isAbilityPublic(member.isAbilityPublic())
                .abilityList(abilityAboutMembers)
                .gender(member.getGender())
                .numberOfMatches(member.getNumberOfMatches())
                .isMajorActivityAreaPublic(member.isMajorActivityAreaPublic())
                .majorActivityArea(member.getMajorActivityArea())
                .oneLineIntroduction(member.getOneLineIntroduction())
                .profilePicId(member.getProfileImgId())
                .build();
    }

    @Override
    @Transactional
    public void setMemberInfo(String memberId, MemberInfoDto memberInfoDto) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUNT.getExceptionMessage()));

        member.setNickname(memberInfoDto.getNickname());
        member.setMajorActivityAreaPublic(memberInfoDto.isMajorActivityAreaPublic());
        member.setMajorActivityArea(memberInfoDto.getMajorActivityArea());
        member.setNumberOfMatches(memberInfoDto.getNumberOfMatches());
        member.setAbilityPublic(memberInfoDto.isAbilityPublic());
        member.setGender(memberInfoDto.getGender());
        member.setOneLineIntroduction(memberInfoDto.getOneLineIntroduction());
        member.setBirthYearPublic(memberInfoDto.isBirthYearPublic());
        member.setBirthYear(member.getBirthYear());
        memberRepository.save(member);

        clearMemberAbility(member);

        if (!memberInfoDto.getAbilityList().isEmpty()) {
            List<MemberAbility> memberAbilityList = memberInfoDto.getAbilityList().stream().map(
                    eachAbilityAboutMember -> MemberAbility.builder()
                            .sportsType(eachAbilityAboutMember.getSportsType())
                            .ability(eachAbilityAboutMember.getAbility())
                            .member(member)
                            .build()
            ).toList();
            memberAbilityRepository.saveAll(memberAbilityList);
        }
    }

    private void clearMemberAbility(Member member) {
        List<MemberAbility> memberAbilitiesByMember = memberAbilityRepository.findMemberAbilitiesByMember(member);
        memberAbilityRepository.deleteAll(memberAbilitiesByMember);
    }

    @Override
    public MemberAuthenticationInfoDto getMemberAuthenticationInfo(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUNT.getExceptionMessage()));
        VerifiedUniversityEmail verifiedUniversityEmail = verifiedUniversityEmailRepository.findByMember(member)
                .orElse(
                        VerifiedUniversityEmail.builder()
                        .univEmailAddress("University authentication has not been completed")
                        .build()
                );
        return MemberAuthenticationInfoDto.builder()
                .memberId(member.getMemberId())
                .univEmail(verifiedUniversityEmail.getUnivEmailAddress())
                .build();
    }

    @Override
    public MemberDto.ResponseMemberInfo getMemberByNickname(Long nextIdx, String nickname) {
        PageRequest pageRequest = PageRequest.of(0, 3);
        List<Member> members = memberRepository.getMemberByNickname(nextIdx, nickname, pageRequest);

        LinkedHashMap<Long, MemberDto.MemberInfoInSearch> membersInfoHashMap = new LinkedHashMap<>();
        members.forEach(member -> membersInfoHashMap.put(member.getPk(), MemberDto.MemberInfoInSearch.builder()
                .nickname(member.getNickname())
                .profileImg(s3ImgService.getMemberProfilePicUrl(member.getProfileImgId()))
                .build()
        ));

        boolean hasNextMember = isExistedNextMember(members, nickname);

        return MemberDto.ResponseMemberInfo.builder()
                .membersInfo(membersInfoHashMap)
                .hasNextMember(hasNextMember)
                .build();
    }

    private boolean isExistedNextMember(List<Member> members, String nickname) {
        Optional<Long> minPk = members.stream().map(Member::getPk).min(Long::compareTo);
        boolean hasNextData = false;
        if (minPk.isPresent()) {
            hasNextData = memberRepository.existsByPkLessThanAndNicknameContains(minPk.get(), nickname);
        }

        return hasNextData;
    }
}