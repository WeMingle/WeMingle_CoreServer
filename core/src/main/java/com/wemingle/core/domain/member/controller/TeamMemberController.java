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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/member/team")
@RequiredArgsConstructor
public class TeamMemberController {
    private final TeamMemberService teamMemberService;

    @GetMapping("/{teamPk}")
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
}
