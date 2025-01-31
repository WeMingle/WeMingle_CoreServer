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

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final TokenService tokenService;

    /**
     * accessToken -> 무조건 재발급
     * refreshToken -> 만료 21일 전에만 재발급
     */
    @PatchMapping("/tokens")
    public ResponseEntity<?> createNewTokens(@RequestBody TokenDto.RequestTokenDto requestTokenDto){
        if (tokenService.isSignInRequired(requestTokenDto)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseHandler.<String>builder()
                        .responseMessage("Please sign in again")
                        .build()
                    );
            }

        TokenDto.ResponseTokenDto responseData = tokenService.createNewTokens(requestTokenDto);

        return ResponseEntity
                .ok(ResponseHandler.<TokenDto.ResponseTokenDto>builder()
                        .responseMessage("Issuance tokens successfully")
                        .responseData(responseData)
                        .build()
                );
    }
}
