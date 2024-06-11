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

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
public class TeamRequestController {
    private final TeamRequestService teamRequestService;

    @GetMapping("/teams/{teamId}/request/members/info")
    public ResponseEntity<ResponseHandler<TeamRequestDto.ResponseRequesterInfo>> getTeamRequestPageInfo(@PathVariable Long teamId,
                                                                                                        @AuthenticationPrincipal UserDetails userDetails){
        TeamRequestDto.ResponseRequesterInfo responseData = teamRequestService.getTeamRequestPageInfo(teamId, userDetails.getUsername());
        return ResponseEntity.ok(
                ResponseHandler.<TeamRequestDto.ResponseRequesterInfo>builder()
                        .responseMessage("Team request page data retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @PostMapping("/teams/request")
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

    @GetMapping("/teams/{teamId}/request/wait")
    public ResponseEntity<ResponseHandler<TeamRequestDto.ResponseTeamRequests>> getPendingTeamRequests(@PathVariable Long teamId) {
        TeamRequestDto.ResponseTeamRequests responseData = teamRequestService.getPendingTeamRequests(teamId);

        return ResponseEntity.ok(
                ResponseHandler.<TeamRequestDto.ResponseTeamRequests>builder()
                        .responseMessage("Team requests retrieval successfully")
                        .responseData(responseData)
                        .build()
                );
    }

    @DeleteMapping("/teams/request")
    public ResponseEntity<Object> deleteTeamRequest(@RequestBody TeamRequestDto.RequestTeamRequestDelete deleteDto) {
        teamRequestService.deleteTeamRequest(deleteDto);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/teams/request/accept")
    public ResponseEntity<Object> approveTeamRequest(@RequestBody TeamRequestDto.RequestTeamRequestApprove approveDto) {
        if (!teamRequestService.isRequestApprovable(approveDto.getTeamId(), approveDto.getTeamRequestId())){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(
                            ResponseHandler.builder()
                                    .responseMessage("Team capacity is exceeded")
                                    .build()
                    );
        }

        teamRequestService.approveTeamRequest(approveDto.getTeamRequestId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/teams/request/{teamRequestId}")
    public ResponseEntity<ResponseHandler<TeamRequestDto.ResponseTeamRequestInfo>> getTeamRequestInfo(@PathVariable Long teamRequestId) {
        TeamRequestDto.ResponseTeamRequestInfo responseData = teamRequestService.getTeamRequestInfo(teamRequestId);

        return ResponseEntity.ok(
                ResponseHandler.<TeamRequestDto.ResponseTeamRequestInfo>builder()
                        .responseMessage("Team request info retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/teams/{teamId}/request/count")
    public ResponseEntity<ResponseHandler<TeamRequestDto.ResponsePendingTeamRequestCnt>> getPendingTeamRequestCnt(@PathVariable Long teamId) {
        TeamRequestDto.ResponsePendingTeamRequestCnt responseData = teamRequestService.getPendingTeamRequestCnt(teamId);

        return ResponseEntity.ok(
                ResponseHandler.<TeamRequestDto.ResponsePendingTeamRequestCnt>builder()
                        .responseMessage("Team request count retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}
