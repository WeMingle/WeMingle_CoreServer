package com.wemingle.core.domain.authentication.service;

import com.wemingle.core.domain.member.entity.Member;
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

    public String createAccessTokenByRefreshToken(String refreshToken){
        Member member = memberService.findByRefreshToken(refreshToken);

        return tokenProvider.generateAccessToken(member.getEmail(), member.getRole());
    }

    @Transactional
    public String createAndPatchRefreshToken(String refreshToken){
        Member member = memberService.findByRefreshToken(refreshToken);

        String newRefreshToken = tokenProvider.generateRefreshToken(member.getEmail(), member.getRole());
        member.patchRefreshToken(newRefreshToken);

        return tokenProvider.generateAccessToken(member.getEmail(), member.getRole());
    }

    public boolean isExpiredAfter21Days(String refreshToken) {
        return tokenProvider.getRemainingTokenExpirationTime(refreshToken).compareTo(Duration.ofDays(21)) < 0;
    }
}
