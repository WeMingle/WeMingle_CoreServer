package com.wemingle.core.domain.team.controller;

import com.wemingle.core.domain.team.dto.TeamDto;
import com.wemingle.core.domain.team.service.TeamMemberService;
import com.wemingle.core.domain.team.service.TeamService;
import com.wemingle.core.global.responseform.ResponseHandler;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {
    private final TeamService teamService;
    private final TeamMemberService teamMemberService;

    @GetMapping("/post")
    public ResponseEntity<ResponseHandler<HashMap<Long, TeamDto.ResponseTeamInfoDto>>> getTeamInfoByMemberId(@AuthenticationPrincipal UserDetails userDetails){
        HashMap<Long, TeamDto.ResponseTeamInfoDto> teamListInfo = teamService.getTeamInfoWithMemberId(userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, TeamDto.ResponseTeamInfoDto>>builder()
                        .responseMessage("Teams info retrieval successfully")
                        .responseData(teamListInfo)
                        .build()
        );
    }

    @GetMapping("/membership")
    public ResponseEntity<ResponseHandler<HashMap<Long, TeamDto.ResponseTeamInfoDto>>> getTeamsAsLeaderOrMember(@AuthenticationPrincipal UserDetails userDetails){
        HashMap<Long, TeamDto.ResponseTeamInfoDto> teamListInfo = teamMemberService.getTeamsAsLeaderOrMember(userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, TeamDto.ResponseTeamInfoDto>>builder()
                        .responseMessage("Teams info retrieval successfully")
                        .responseData(teamListInfo)
                        .build()
        );
    }

    @GetMapping("/existence")
    public ResponseEntity<ResponseHandler<Object>> isPresentTeamWithMe(@AuthenticationPrincipal UserDetails userDetails){
        boolean isPresentTeam = teamService.isPresentTeamWithMe(userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.builder()
                        .responseMessage("Check existence of my team successfully")
                        .responseData(isPresentTeam)
                        .build()
        );
    }

    @GetMapping("/random")
    public ResponseEntity<ResponseHandler<HashMap<Long, TeamDto.ResponseRandomTeamInfo>>> getRandomTeams(){
        HashMap<Long, TeamDto.ResponseRandomTeamInfo> randomTeams = teamService.getRandomTeam();

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, TeamDto.ResponseRandomTeamInfo>>builder()
                        .responseMessage("Random teams retrieval successfully")
                        .responseData(randomTeams)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ResponseHandler<TeamDto.ResponseTeamInfoByName>> getTeamsByTeamName(@RequestParam(required = false) Long nextIdx,
                                                                                              @RequestParam @NotNull @NotEmpty @NotBlank String teamName){
        TeamDto.ResponseTeamInfoByName responseData = teamService.getTeamByName(nextIdx, teamName);

        return ResponseEntity.ok(
                ResponseHandler.<TeamDto.ResponseTeamInfoByName>builder()
                        .responseMessage("Teams retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}
