package com.wemingle.core.domain.authentication.controller;

import com.wemingle.core.domain.authentication.dto.TokenDto;
import com.wemingle.core.domain.authentication.service.TokenService;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.wemingle.core.global.exceptionmessage.ExceptionMessage.IS_EXPIRED_REFRESH_AND_ACCESS_TOKEN;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final TokenService tokenService;

    @PatchMapping("/token")
    public ResponseEntity<?> updateToken(@RequestBody TokenDto.RequestTokenDto requestTokenDto){
        String refreshToken = requestTokenDto.getRefreshToken();
        String accessToken = requestTokenDto.getAccessToken();

        if (tokenService.verifyRefreshAndAccessToken(refreshToken, accessToken)){
            if (tokenService.isExpiredRefreshAndAccessToken(refreshToken, accessToken)){
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(ResponseHandler.<String>builder()
                            .responseMessage(IS_EXPIRED_REFRESH_AND_ACCESS_TOKEN.getExceptionMessage())
                            .responseData(null)
                            .build()
                        );
            }
        }

        TokenDto.ResponseTokenDto.ResponseTokenDtoBuilder responseTokenDtoBuilder = TokenDto.ResponseTokenDto.builder();
        if (tokenService.verifyRefreshToken(refreshToken)) {
            String newAccessToken = tokenService.createAccessTokenByRefreshToken(refreshToken);
            responseTokenDtoBuilder.accessToken(newAccessToken);
            responseTokenDtoBuilder.accessTokenExpiredTime(tokenService.getExpirationTime(newAccessToken));


            if (tokenService.isExpiredAfter21Days(refreshToken)) {
                String newRefreshToken = tokenService.createAndPatchRefreshTokenInMember(refreshToken);
                responseTokenDtoBuilder.refreshToken(newRefreshToken);
                responseTokenDtoBuilder.refreshTokenExpiredTime(tokenService.getExpirationTime(newRefreshToken));
            }
        }


        TokenDto.ResponseTokenDto responseTokenDto = responseTokenDtoBuilder.build();

        return ResponseEntity
                .ok(ResponseHandler.<TokenDto.ResponseTokenDto>builder()
                        .responseMessage(generateResponseMessage(responseTokenDto))
                        .responseData(responseTokenDto)
                        .build()
                );
    }

    private static String generateResponseMessage(TokenDto.ResponseTokenDto responseTokenDto){
        return responseTokenDto.getRefreshToken() == null ? "Reissuance Access Token Completed" : "Reissuance Refresh and Access Token completed";
    }
}
