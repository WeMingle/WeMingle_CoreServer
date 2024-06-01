package com.wemingle.core.domain.matching.controller;


import com.wemingle.core.domain.matching.dto.TeamRequestDto;
import com.wemingle.core.domain.matching.service.TeamRequestService;
import com.wemingle.core.global.responseform.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

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
        if (!teamRequestService.isRequestableTeam(requestSaveDto.getTeamPk())){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(
                            ResponseHandler.builder()
                                    .responseMessage("Team capacity is exceeded")
                                    .build()
                    );
        }

        teamRequestService.saveTeamMemberOrRequestByRecruitmentType(requestSaveDto, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ResponseHandler<TeamRequestDto.ResponseTeamRequests>> getPendingTeamRequests(@RequestParam Long teamPk) {
        TeamRequestDto.ResponseTeamRequests responseData = teamRequestService.getPendingTeamRequests(teamPk);

        return ResponseEntity.ok(
                ResponseHandler.<TeamRequestDto.ResponseTeamRequests>builder()
                        .responseMessage("Team requests retrieval successfully")
                        .responseData(responseData)
                        .build()
                );
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteTeamRequest(@RequestBody TeamRequestDto.RequestTeamRequestDelete deleteDto) {
        teamRequestService.deleteTeamRequest(deleteDto);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/approval")
    public ResponseEntity<Object> approveTeamRequest(@RequestBody TeamRequestDto.RequestTeamRequestApprove approveDto) {
        if (!teamRequestService.isRequestApprovable(approveDto.getTeamPk(), approveDto.getTeamRequestPk())){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(
                            ResponseHandler.builder()
                                    .responseMessage("Team capacity is exceeded")
                                    .build()
                    );
        }

        teamRequestService.approveTeamRequest(approveDto.getTeamRequestPk());
        return ResponseEntity.noContent().build();
    }
}
