package com.wemingle.core.domain.authentication.service;

import com.wemingle.core.domain.authentication.dto.TokenDto;
import com.wemingle.core.domain.member.dto.SignInDto;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.role.Role;
import com.wemingle.core.domain.member.service.MemberService;
import com.wemingle.core.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {
    private final TokenProvider tokenProvider;
    private final MemberService memberService;


    public boolean isVerifiedRefreshAndAccessToken(String refreshToken, String accessToken) {
        return tokenProvider.validToken(refreshToken) && tokenProvider.validToken(accessToken);
    }

    public boolean isVerifiedRefreshToken(String refreshToken) {
        return tokenProvider.validToken(refreshToken);
    }

    public boolean isExpiredRefreshAndAccessToken(String refreshToken, String accessToken) {
        return tokenProvider.isExpired(refreshToken) && tokenProvider.isExpired(accessToken);
    }

    public boolean isSignInRequired(TokenDto.RequestTokenDto requestTokenDto) {
        String refreshToken = requestTokenDto.getRefreshToken();
        String accessToken = requestTokenDto.getAccessToken();

        return !isVerifiedRefreshAndAccessToken(refreshToken, accessToken) && isExpiredRefreshAndAccessToken(refreshToken, accessToken);
    }

    public Date getExpirationTime(String jwtToken) {
        return tokenProvider.getExpirationTime(jwtToken);
    }

    public TokenDto.ResponseTokenDto getUnVerifiedUserTokens(String memberId){
        String refreshToken = tokenProvider.createRefreshToken(memberId, Role.UNVERIFIED_USER);
        String accessToken = tokenProvider.createAccessToken(memberId, Role.UNVERIFIED_USER);
        return TokenDto.ResponseTokenDto.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .refreshTokenExpiredTime(getExpirationTime(refreshToken))
                .accessTokenExpiredTime(getExpirationTime(accessToken))
                .build();
    }

    public String createAccessTokenByRefreshToken(String refreshToken){
        Member member = memberService.findByRefreshToken(refreshToken);

        return tokenProvider.createAccessToken(member.getMemberId(), member.getRole());
    }

    @Transactional
    public String createAndPatchRefreshTokenInMember(String refreshToken){
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
    public SignInDto.ResponseSignInDto getTokensForRegisteredMember(String memberId) {
        Member member = memberService.findByMemberId(memberId);

        String accessToken = tokenProvider.createAccessToken(memberId, member.getRole());
        String refreshToken = tokenProvider.createRefreshToken(memberId, member.getRole());
        boolean isEmailVerified = member.getRole().equals(Role.USER);
        boolean isOnboardingComplete = !Objects.isNull(member.getPreferenceSport());


        member.patchRefreshToken(refreshToken);

        TokenDto.ResponseTokenDto responseTokenDto = TokenDto.ResponseTokenDto.builder()
                .refreshToken(refreshToken)
                .refreshTokenExpiredTime(getExpirationTime(refreshToken))
                .accessToken(accessToken)
                .accessTokenExpiredTime(getExpirationTime(accessToken))
                .build();

        return SignInDto.ResponseSignInDto.builder()
                .token(responseTokenDto)
                .isEmailVerified(isEmailVerified)
                .isOnboardingComplete(isOnboardingComplete)
                .build();
    }

    private void convertToAuthenticationUser(Member member, String newRefreshToken){
        member.convertToAuthenticationUser(newRefreshToken);
    }

    public boolean isExpiredAfter21Days(String refreshToken) {
        return tokenProvider.getRemainingTokenExpirationTime(refreshToken).compareTo(Duration.ofDays(21)) < 0;
    }

    @Transactional
    public TokenDto.ResponseTokenDto createNewTokens(String refreshToken) {
        TokenDto.ResponseTokenDto.ResponseTokenDtoBuilder responseTokenDtoBuilder = TokenDto.ResponseTokenDto.builder();

        if (isVerifiedRefreshToken(refreshToken)) {
            String newAccessToken = createAccessTokenByRefreshToken(refreshToken);
            responseTokenDtoBuilder.accessToken(newAccessToken);
            responseTokenDtoBuilder.accessTokenExpiredTime(getExpirationTime(newAccessToken));

            if (isExpiredAfter21Days(refreshToken)) {
                String newRefreshToken = createAndPatchRefreshTokenInMember(refreshToken);
                responseTokenDtoBuilder.refreshToken(newRefreshToken);
                responseTokenDtoBuilder.refreshTokenExpiredTime(getExpirationTime(newRefreshToken));
            }
        }

        return responseTokenDtoBuilder.build();
    }
}
