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

import static com.wemingle.core.global.exceptionmessage.ExceptionMessage.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final TokenService tokenService;
    private static String ISSUE_ACCESS_TOKEN = "access token을 발급하였습니다.";


    @PatchMapping("/token")
    public ResponseEntity<?> patchToken(@RequestBody TokenDto.RequestTokenDto requestTokenDto){
        String refreshToken = requestTokenDto.getRefreshToken();
        String accessToken = requestTokenDto.getAccessToken();

        if (tokenService.isInvalidRefreshAndAccessToken(refreshToken, accessToken)){
            if (tokenService.isExpiredRefreshAndAccessToken(refreshToken, accessToken)){
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(ResponseHandler.<String>builder()
                            .responseMessage(IS_EXPIRED_REFRESH_AND_ACCESS_TOKEN.getExceptionMessage())
                            .responseData(null)
                        );
            }
        }

        TokenDto.ResponseTokenDto.ResponseTokenDtoBuilder responseTokenDtoBuilder = TokenDto.ResponseTokenDto.builder();
        if (tokenService.isInvalidAccessToken(refreshToken, accessToken)){
            if (tokenService.isExpiredAccessToken(accessToken)){
                String newAccessToken = tokenService.createAccessTokenByRefreshToken(refreshToken);
                responseTokenDtoBuilder.accessToken(newAccessToken);
            }

            if (tokenService.isExpiredAfter21Days(refreshToken)){
                String newRefreshToken = tokenService.createAndPatchRefreshToken(refreshToken);
                responseTokenDtoBuilder.refreshToken(newRefreshToken);
            }
        }

        TokenDto.ResponseTokenDto responseTokenDto = responseTokenDtoBuilder.build();

        if (isActiveAccessToken(responseTokenDto.getAccessToken())){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity
                .ok(ResponseHandler.<TokenDto.ResponseTokenDto>builder()
                        .responseMessage(generateResponseMessage(responseTokenDto))
                        .responseData(responseTokenDto)
                );
    }

    private static boolean isActiveAccessToken(String accessToken){
        return accessToken == null;
    }
    private static String generateResponseMessage(TokenDto.ResponseTokenDto responseTokenDto){
        return responseTokenDto.getRefreshToken() == null ? "Access Token이 재발급되었습니다." : "Refresh와 Access Token이 재발급되었습니다";
    }
}
