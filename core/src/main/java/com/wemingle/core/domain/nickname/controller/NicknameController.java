package com.wemingle.core.domain.nickname.controller;

import com.wemingle.core.domain.nickname.service.NicknameService;
import com.wemingle.core.global.responseform.ResponseHandler;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Slf4j
@RequestMapping("/nickname")
@RestController
@RequiredArgsConstructor
public class NicknameController {

    private final NicknameService nicknameService;
    @GetMapping("/{nickname}/availability")
    ResponseEntity<ResponseHandler<Object>> checkNickname(@PathVariable("nickname")
                                                     @Size(min = 2, max = 10, message = "2~10글자 사이로 입력하세요")
                                                     String nickname) {
        if (nicknameService.isAvailableNickname(nickname)) {
            return ResponseEntity.ok(ResponseHandler.builder().responseMessage("사용 가능한 닉네임입니다.").build());
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseHandler.builder().responseMessage("사용할 수 없는 닉네임입니다.").build());

    }

    @GetMapping("/team/{teamPk}/{nickname}/availability")
    public ResponseEntity<ResponseHandler<Object>> checkTeamMemberNickname(@PathVariable Long teamPk,
                                                                           @PathVariable("nickname")
                                                                           @Size(min = 2, max = 10, message = "2~10글자 사이로 입력하세요")
                                                                           String nickname) {
        if (nicknameService.isAvailableNicknameInTeam(teamPk, nickname)) {
            return ResponseEntity.ok(ResponseHandler.builder().responseMessage("사용 가능한 닉네임입니다.").build());
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseHandler.builder().responseMessage("사용할 수 없는 닉네임입니다.").build());
    }
}
