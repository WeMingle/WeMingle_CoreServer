package com.wemingle.core.domain.authentication.service;

import com.wemingle.core.domain.authentication.dto.TokenDto;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.role.Role;
import com.wemingle.core.domain.member.service.MemberService;
import com.wemingle.core.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {
    private final TokenProvider tokenProvider;
    private final MemberService memberService;


    public boolean isInvalidRefreshAndAccessToken(String refreshToken, String accessToken) {
        return !tokenProvider.validToken(refreshToken) && !tokenProvider.validToken(accessToken);
    }

    public boolean isInvalidAccessToken(String refreshToken, String accessToken) {
        return tokenProvider.validToken(refreshToken) && !tokenProvider.validToken(accessToken);
    }

    public boolean isExpiredRefreshAndAccessToken(String refreshToken, String accessToken) {
        return tokenProvider.isExpired(refreshToken) && tokenProvider.isExpired(accessToken);
    }

    public boolean isExpiredAccessToken(String accessToken) {
        return tokenProvider.isExpired(accessToken);
    }

    public TokenDto.ResponseTokenDto getUnVerifiedUserTokens(String memberId){
        return TokenDto.ResponseTokenDto.builder()
                .refreshToken(tokenProvider.createRefreshToken(memberId, Role.UNVERIFIED_USER))
                .accessToken(tokenProvider.createAccessToken(memberId, Role.UNVERIFIED_USER))
                .build();
    }

    public String getAccessTokenByRefreshToken(String refreshToken){
        Member member = memberService.findByRefreshToken(refreshToken);

        return tokenProvider.createAccessToken(member.getMemberId(), member.getRole());
    }

    @Transactional
    public String getAndPatchRefreshTokenInMember(String refreshToken){
        Member member = memberService.findByRefreshToken(refreshToken);

        String newRefreshToken = tokenProvider.createRefreshToken(member.getMemberId(), member.getRole());
        member.patchRefreshToken(newRefreshToken);

        return tokenProvider.createRefreshToken(member.getMemberId(), member.getRole());
    }

    @Transactional
    public TokenDto.ResponseTokenDto getTokensAndConvertToAuthenticationUser(Member member){
        String memberId = member.getMemberId();
        String newRefreshToken = tokenProvider.createRefreshToken(memberId, Role.USER);
        String newAccessToken = tokenProvider.createAccessToken(memberId, Role.USER);

        convertToAuthenticationUser(member, newRefreshToken);

        return TokenDto.ResponseTokenDto.builder()
                .refreshToken(newRefreshToken)
                .accessToken(newAccessToken)
                .build();
    }

    @Transactional
    public TokenDto.ResponseTokenDto getTokensForRegisteredMember(String memberId) {
        Member member = memberService.findByMemberId(memberId);

        String accessToken = tokenProvider.createAccessToken(memberId, member.getRole());
        String refreshToken = tokenProvider.createRefreshToken(memberId, member.getRole());

        member.patchRefreshToken(refreshToken);
        return TokenDto.ResponseTokenDto.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();
    }

    private void convertToAuthenticationUser(Member member, String newRefreshToken){
        member.convertToAuthenticationUser(newRefreshToken);
    }

    public boolean isExpiredAfter21Days(String refreshToken) {
        return tokenProvider.getRemainingTokenExpirationTime(refreshToken).compareTo(Duration.ofDays(21)) < 0;
    }
}
