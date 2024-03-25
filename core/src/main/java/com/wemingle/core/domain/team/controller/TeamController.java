package com.wemingle.core.domain.team.controller;

import com.wemingle.core.domain.team.dto.TeamDto;
import com.wemingle.core.domain.team.service.TeamService;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {
    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<ResponseHandler<HashMap<Long, TeamDto.ResponseTeamInfoDto>>> getTeamInfoByMemberId(@AuthenticationPrincipal UserDetails userDetails){
        HashMap<Long, TeamDto.ResponseTeamInfoDto> teamListInfo = teamService.getTeamInfoWithMemberId(userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, TeamDto.ResponseTeamInfoDto>>builder()
                        .responseMessage("Teams info retrieval successfully")
                        .responseData(teamListInfo)
                        .build()
        );
    }
}
