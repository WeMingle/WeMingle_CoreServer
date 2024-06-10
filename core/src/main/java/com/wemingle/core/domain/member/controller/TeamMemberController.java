package com.wemingle.core.domain.member.controller;

import com.wemingle.core.domain.member.dto.TeamMemberDto;
import com.wemingle.core.domain.member.service.TeamMemberLeaveService;
import com.wemingle.core.domain.team.service.TeamMemberService;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TeamMemberController {
    private final TeamMemberService teamMemberService;
    private final TeamMemberLeaveService teamMemberLeaveService;

    @GetMapping("/members/teams/{teamId}")
    public ResponseEntity<ResponseHandler<HashMap<Long, TeamMemberDto.ResponseTeamMembers>>> getTeamMembersInTeam(@PathVariable Long teamId,
                                                                                                                  @AuthenticationPrincipal UserDetails userDetails){
        HashMap<Long, TeamMemberDto.ResponseTeamMembers> responseData = teamMemberService.getTeamMembersInTeam(teamId, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, TeamMemberDto.ResponseTeamMembers>>builder()
                        .responseMessage("Team members retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/members/{teamMemberId}/team")
    public ResponseEntity<ResponseHandler<TeamMemberDto.ResponseTeamMemberProfile>> getTeamMemberProfile(@PathVariable Long teamMemberId) {
        TeamMemberDto.ResponseTeamMemberProfile responseData = teamMemberService.getTeamMemberProfile(teamMemberId);

        return ResponseEntity.ok(
                ResponseHandler.<TeamMemberDto.ResponseTeamMemberProfile>builder()
                        .responseMessage("Team member profile retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @PatchMapping("/members/teams/profile")
    public ResponseEntity<Object> updateTeamMemberProfile(@RequestBody TeamMemberDto.RequestTeamMemberProfileUpdate updateDto) {
        teamMemberService.updateTeamMemberProfile(updateDto);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/members/{teamMemberId}/teams/participant")
    public ResponseEntity<Object> updateManagerRoleToLower(@PathVariable Long teamMemberId) {
        if (!teamMemberService.isExistOtherManager(teamMemberId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(
                            ResponseHandler.builder()
                                    .responseMessage("Cannot remove manager role in a team with only one manager")
                                    .build());
        }

        teamMemberService.updateManagerRoleToLower(teamMemberId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/members/teams/manager")
    public ResponseEntity<Object> updateParticipantRoleToHigher(@RequestBody TeamMemberDto.RequestTeamMemberRoleToManagerUpdate updateUto) {
        teamMemberService.updateParticipantRoleToHigher(updateUto.getGrantorId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/members/teams/block")
    public ResponseEntity<Object> blockTeamMember(@RequestBody TeamMemberDto.RequestTeamMemberBlock blockDto) {
        teamMemberService.blockTeamMember(blockDto.getBlockedMemberId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/members/teams/{teamId}/info")
    public ResponseEntity<ResponseHandler<HashMap<Long, TeamMemberDto.ResponseTeamMemberInfo>>> getAllTeamMembersInfo(@PathVariable Long teamId,
                                                                                                                      @AuthenticationPrincipal UserDetails userDetails) {
        HashMap<Long, TeamMemberDto.ResponseTeamMemberInfo> responseData = teamMemberService.getAllTeamMembersInfo(teamId, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, TeamMemberDto.ResponseTeamMemberInfo>>builder()
                        .responseMessage("Team members info retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @DeleteMapping("/members/{teamMemberId}/teams")
    public ResponseEntity<ResponseHandler<Object>> withdrawTeamMember(@PathVariable Long teamMemberId,
                                                                      @AuthenticationPrincipal UserDetails userDetails) {
        teamMemberLeaveService.withdrawTeamMember(teamMemberId, userDetails.getUsername());

        return ResponseEntity.noContent().build();
    }
}
