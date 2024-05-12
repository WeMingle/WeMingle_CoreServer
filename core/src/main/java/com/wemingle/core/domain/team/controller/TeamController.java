package com.wemingle.core.domain.team.controller;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.team.dto.CreateTeamDto;
import com.wemingle.core.domain.team.dto.TeamDto;
import com.wemingle.core.domain.team.service.TeamMemberService;
import com.wemingle.core.domain.team.service.TeamService;
import com.wemingle.core.global.responseform.ResponseHandler;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {
    private final TeamService teamService;
    private final TeamMemberService teamMemberService;

    @GetMapping("/profile/writable")
    public ResponseEntity<ResponseHandler<HashMap<Long, TeamDto.ResponseWritableTeamInfoDto>>> getTeamInfoByMemberId(@RequestParam SportsType sportsType,
                                                                                                                     @AuthenticationPrincipal UserDetails userDetails){
        HashMap<Long, TeamDto.ResponseWritableTeamInfoDto> teamListInfo = teamService.getTeamInfoWithAvailableWrite(sportsType, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, TeamDto.ResponseWritableTeamInfoDto>>builder()
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

    @GetMapping("/home/condition")
    public ResponseEntity<ResponseHandler<TeamDto.ResponseTeamHomeConditions>> verifyTeamHomeConditions(@AuthenticationPrincipal UserDetails userDetails){
        TeamDto.ResponseTeamHomeConditions teamHomeConditions = teamService.getTeamHomeConditions(userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<TeamDto.ResponseTeamHomeConditions>builder()
                        .responseMessage("Check existence of my team successfully")
                        .responseData(teamHomeConditions)
                        .build()
        );
    }

    @GetMapping("/recommendation")
    public ResponseEntity<ResponseHandler<HashMap<Long, TeamDto.ResponseRecommendationTeamInfo>>> getRecommendTeams(@RequestParam(required = false) Long nextIdx,
                                                                                                                             @AuthenticationPrincipal UserDetails userDetails){
        HashMap<Long, TeamDto.ResponseRecommendationTeamInfo> randomTeams = teamService.getRecommendTeams(nextIdx, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, TeamDto.ResponseRecommendationTeamInfo>>builder()
                        .responseMessage("Recommendation teams retrieval successfully")
                        .responseData(randomTeams)
                        .build()
        );
    }

    @GetMapping("/recommendation/member")
    public ResponseEntity<ResponseHandler<HashMap<Long, TeamDto.ResponseRecommendationTeamForMemberInfo>>> getRecommendTeamsForMember(@RequestParam(required = false) Long nextIdx,
                                                                                                                                      @AuthenticationPrincipal UserDetails userDetails){
        HashMap<Long, TeamDto.ResponseRecommendationTeamForMemberInfo> randomTeams = teamService.getRecommendTeamsForMember(nextIdx, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, TeamDto.ResponseRecommendationTeamForMemberInfo>>builder()
                        .responseMessage("Recommendation teams for member retrieval successfully")
                        .responseData(randomTeams)
                        .build()
        );
    }

    @GetMapping("/result")
    public ResponseEntity<ResponseHandler<HashMap<Long, TeamDto.ResponseTeamInfoInSearch>>> getTeamsByTeamName(@RequestParam(required = false) Long nextIdx,
                                                                                              @RequestParam @NotBlank String query){
        String teamName = URLDecoder.decode(query, StandardCharsets.UTF_8);
        HashMap<Long, TeamDto.ResponseTeamInfoInSearch> responseData = teamService.getTeamByName(nextIdx, teamName);

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, TeamDto.ResponseTeamInfoInSearch>>builder()
                        .responseMessage("Teams retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/univ")
    public ResponseEntity<ResponseHandler<HashMap<Long, TeamDto.ResponseTeamByMemberUniv>>> getTeamsWithMyUniv(@RequestParam(required = false) Long nextIdx,
                                                                                                               @AuthenticationPrincipal UserDetails userDetails){
        HashMap<Long, TeamDto.ResponseTeamByMemberUniv> responseData = teamService.getTeamWithMemberUniv(nextIdx, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, TeamDto.ResponseTeamByMemberUniv>>builder()
                        .responseMessage("Teams associated with my univ retrieval successfully")
                        .responseData(responseData)
                        .build());
    }

    @GetMapping("/{teamPk}")
    public ResponseEntity<ResponseHandler<TeamDto.TeamInfo>> getTeamInfoWithTeam(@PathVariable Long teamPk,
                                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        TeamDto.TeamInfo responseData = teamService.getTeamInfoWithTeam(teamPk, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<TeamDto.TeamInfo>builder()
                        .responseMessage("Team info retrieval successfully")
                        .responseData(responseData)
                        .build());
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseHandler<Object>> createTeam(@RequestBody CreateTeamDto createTeamDto,
                                                                       @AuthenticationPrincipal UserDetails userDetails) {
        teamService.saveTeam(userDetails.getUsername(),createTeamDto);
        return ResponseEntity.ok(
                ResponseHandler.builder().responseMessage("Team create successfully").build()
        );
    }

    @GetMapping("/{teamPk}/condition")
    public ResponseEntity<ResponseHandler<TeamDto.ResponseTeamParticipantCond>> getTeamParticipantCond(@PathVariable Long teamPk,
                                                                                                       @AuthenticationPrincipal UserDetails userDetails){
        TeamDto.ResponseTeamParticipantCond responseData = teamService.getTeamParticipantCond(teamPk, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<TeamDto.ResponseTeamParticipantCond>builder()
                        .responseMessage("Team condition result retrieval successfully")
                        .responseData(responseData)
                        .build());
    }

    @GetMapping("/profile/requestable")
    public ResponseEntity<ResponseHandler<HashMap<Long, TeamDto.ResponseWritableTeamInfoDto>>> getRequestableTeamsInfo(@RequestParam Long matchingPostPk,
                                                                                                                       @AuthenticationPrincipal UserDetails userDetails){
        HashMap<Long, TeamDto.ResponseWritableTeamInfoDto> teamListInfo = teamService.getRequestableTeamsInfo(matchingPostPk, userDetails.getUsername());

        return ResponseEntity.ok(
                ResponseHandler.<HashMap<Long, TeamDto.ResponseWritableTeamInfoDto>>builder()
                        .responseMessage("Teams info retrieval successfully")
                        .responseData(teamListInfo)
                        .build()
        );
    }

    @GetMapping("/setting/{teamPk}")
    public ResponseEntity<ResponseHandler<TeamDto.ResponseTeamSetting>> getTeamSetting(@PathVariable Long teamPk){
        TeamDto.ResponseTeamSetting responseData = teamService.getTeamSetting(teamPk);

        return ResponseEntity.ok(
                ResponseHandler.<TeamDto.ResponseTeamSetting>builder()
                        .responseMessage("Team setting info retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @PatchMapping("/setting")
    public ResponseEntity<Object> updateTeamSetting(@RequestBody TeamDto.RequestTeamSettingUpdate updateDto){
        teamService.updateTeamSetting(updateDto);

        return ResponseEntity.noContent().build();
    }
}
