package com.wemingle.core.domain.matching.controller;


import com.wemingle.core.domain.matching.dto.TeamRequestDto;
import com.wemingle.core.domain.matching.service.TeamRequestService;
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

@Slf4j
@RestController
@RequestMapping("/team/request")
@RequiredArgsConstructor
public class TeamRequestController {
    private final TeamRequestService teamRequestService;

    @GetMapping("/member/info/{teamPk}")
    private ResponseEntity<ResponseHandler<TeamRequestDto.ResponseRequesterInfo>> getTeamRequestPageInfo(@PathVariable Long teamPk,
                                                                                                         @AuthenticationPrincipal UserDetails userDetails){
        TeamRequestDto.ResponseRequesterInfo responseData = teamRequestService.getTeamRequestPageInfo(teamPk, userDetails.getUsername());
        return ResponseEntity.ok(
                ResponseHandler.<TeamRequestDto.ResponseRequesterInfo>builder()
                        .responseMessage("Team request page data retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}
