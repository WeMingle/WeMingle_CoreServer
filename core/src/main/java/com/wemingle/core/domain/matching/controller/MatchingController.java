package com.wemingle.core.domain.matching.controller;

import com.wemingle.core.domain.post.dto.MatchingPostDto;
import com.wemingle.core.domain.post.service.MatchingPostService;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/match")
@RequiredArgsConstructor
public class MatchingController {
    private final MatchingPostService matchingPostService;
    @PostMapping
    ResponseEntity<ResponseHandler<Object>> createMatchingPost(@RequestBody MatchingPostDto.CreateMatchingPostDto matchingPostDto,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        matchingPostService.createMatchingPost(matchingPostDto, userDetails.getUsername());
        return ResponseEntity.ok()
                .body(
                        ResponseHandler.builder()
                                .responseMessage("matching post successfully created")
                                .build()
                );
    }
}
