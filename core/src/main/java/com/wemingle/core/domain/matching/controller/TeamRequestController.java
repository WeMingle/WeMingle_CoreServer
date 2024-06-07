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

    @GetMapping("/team/{teamPk}/request/member/info")
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

    @PostMapping("/team/request")
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

    @GetMapping("/team/{teamPk}/request")
    public ResponseEntity<ResponseHandler<TeamRequestDto.ResponseTeamRequests>> getPendingTeamRequests(@PathVariable Long teamPk) {
        TeamRequestDto.ResponseTeamRequests responseData = teamRequestService.getPendingTeamRequests(teamPk);

        return ResponseEntity.ok(
                ResponseHandler.<TeamRequestDto.ResponseTeamRequests>builder()
                        .responseMessage("Team requests retrieval successfully")
                        .responseData(responseData)
                        .build()
                );
    }

    @DeleteMapping("/team/request")
    public ResponseEntity<Object> deleteTeamRequest(@RequestBody TeamRequestDto.RequestTeamRequestDelete deleteDto) {
        teamRequestService.deleteTeamRequest(deleteDto);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/team/request/approval")
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

    @GetMapping("/team/request/{teamRequestPk}")
    public ResponseEntity<ResponseHandler<TeamRequestDto.ResponseTeamRequestInfo>> getTeamRequestInfo(@PathVariable Long teamRequestPk) {
        TeamRequestDto.ResponseTeamRequestInfo responseData = teamRequestService.getTeamRequestInfo(teamRequestPk);

        return ResponseEntity.ok(
                ResponseHandler.<TeamRequestDto.ResponseTeamRequestInfo>builder()
                        .responseMessage("Team request info retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/team/{teamPk}/request/count")
    public ResponseEntity<ResponseHandler<TeamRequestDto.ResponsePendingTeamRequestCnt>> getPendingTeamRequestCnt(@PathVariable Long teamPk) {
        TeamRequestDto.ResponsePendingTeamRequestCnt responseData = teamRequestService.getPendingTeamRequestCnt(teamPk);

        return ResponseEntity.ok(
                ResponseHandler.<TeamRequestDto.ResponsePendingTeamRequestCnt>builder()
                        .responseMessage("Team request count retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}
