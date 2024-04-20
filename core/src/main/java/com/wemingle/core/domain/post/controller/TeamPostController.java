package com.wemingle.core.domain.post.controller;

import com.wemingle.core.domain.post.dto.TeamPostDto;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post/team")
public class TeamPostController {

    @GetMapping("/all")
    public ResponseEntity<ResponseHandler<TeamPostDto.ResponseTeamPostsInfoWithMember>> getTeamPostsWithMember(){
        return null;
    }
}
