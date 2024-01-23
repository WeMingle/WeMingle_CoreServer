package com.wemingle.core.domain.nickname.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.wemingle.core.domain.nickname.service.NicknameService;
import com.wemingle.core.global.responseform.ResponseHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@Slf4j
@RequestMapping("/nickname")
@RestController
@RequiredArgsConstructor
public class NicknameController {

    private final NicknameService nicknameService;

    @GetMapping("/{count}")
    ResponseEntity<ResponseHandler<List<String>>> getNickname(@PathVariable("count") @Min (value = 1,message = "1 이상 요청하세요") @Max (value = 10, message = "10 이하 요청하세요") int count) {
        List<String> generateNicknames = nicknameService.generateNicknames(count);
        return ResponseEntity.ok().body(
                ResponseHandler.<List<String>>builder()
                        .responseMessage("success")
                        .responseData(generateNicknames)
                        .build());
    }
}
