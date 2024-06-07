package com.wemingle.core.domain.matching.controller;

import com.wemingle.core.domain.matching.service.MatchingService;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;

@Slf4j
@RestController
@RequestMapping("/match")
@RequiredArgsConstructor
public class MatchingController {
    private final MatchingService matchingService;

    @GetMapping("/summaries")
    ResponseEntity<ResponseHandler<Object>> getMyMatchingSummary(@AuthenticationPrincipal UserDetails userDetails) {
        LinkedHashMap<String, Integer> matchingSummaryInfo = matchingService.getMatchingSummaryInfo(userDetails.getUsername());

        return ResponseEntity.ok().body(
                ResponseHandler.builder()
                        .responseMessage("match summaries retrieval successfully")
                        .responseData(matchingSummaryInfo).build()
        );
    }
}
