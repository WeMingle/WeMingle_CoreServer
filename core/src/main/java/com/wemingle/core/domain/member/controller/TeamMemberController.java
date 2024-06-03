package com.wemingle.core.domain.member.controller;

import com.wemingle.core.domain.member.dto.TeamMemberDto;
import com.wemingle.core.domain.team.service.TeamMemberService;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TeamMemberController {
    private final TeamMemberService teamMemberService;

    @GetMapping("/member/team/{teamPk}")
    public ResponseEntity<ResponseHandler<HashMap<Long, TeamMemberDto.ResponseTeamMembers>>> getTeamMembersInTeam(@PathVariable Long teamPk,
                                                                                                                  @AuthenticationPrincipal UserDetails userDetails){
        HashMap<Long, TeamMemberDto.ResponseTeamMembers> responseData = teamMemberService.getTeamMembersInTeam(teamPk, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, TeamMemberDto.ResponseTeamMembers>>builder()
                        .responseMessage("Team members retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/member/{teamMemberPk}/team")
    public ResponseEntity<ResponseHandler<TeamMemberDto.ResponseTeamMemberInfo>> getTeamMemberInfo(@PathVariable Long teamMemberPk) {
        TeamMemberDto.ResponseTeamMemberInfo responseData = teamMemberService.getTeamMemberInfo(teamMemberPk);

        return ResponseEntity.ok(
                ResponseHandler.<TeamMemberDto.ResponseTeamMemberInfo>builder()
                        .responseMessage("Team member info retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}
