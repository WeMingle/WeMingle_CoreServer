package com.wemingle.core.domain.matching.controller;


import com.wemingle.core.domain.matching.dto.TeamRequestDto;
import com.wemingle.core.domain.matching.service.TeamRequestService;
import com.wemingle.core.global.responseform.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Slf4j
@RestController
@RequestMapping("/team/request")
@RequiredArgsConstructor
public class TeamRequestController {
    private final TeamRequestService teamRequestService;

    @GetMapping("/member/info/{teamPk}")
    public ResponseEntity<ResponseHandler<TeamRequestDto.ResponseRequesterInfo>> getTeamRequestPageInfo(@PathVariable Long teamPk,
                                                                                                         @AuthenticationPrincipal UserDetails userDetails){
        TeamRequestDto.ResponseRequesterInfo responseData = teamRequestService.getTeamRequestPageInfo(teamPk, userDetails.getUsername());
        return ResponseEntity.ok(
                ResponseHandler.<TeamRequestDto.ResponseRequesterInfo>builder()
                        .responseMessage("Team request page data retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ResponseHandler<Object>> saveTeamRequest(@RequestBody @Valid TeamRequestDto.RequestTeamRequestSave requestSaveDto,
                                                                   @AuthenticationPrincipal UserDetails userDetails){
        teamRequestService.saveTeamMemberOrRequestByRecruitmentType(requestSaveDto, userDetails.getUsername());

        return ResponseEntity.noContent().build();
    }
}