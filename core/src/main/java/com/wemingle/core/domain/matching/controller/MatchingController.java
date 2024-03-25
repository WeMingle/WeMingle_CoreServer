package com.wemingle.core.domain.matching.controller;

import com.wemingle.core.domain.post.service.MatchingPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/match")
@RequiredArgsConstructor
public class MatchingController {
    private final MatchingPostService matchingPostService;

}
