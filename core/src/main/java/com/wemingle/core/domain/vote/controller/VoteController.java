package com.wemingle.core.domain.vote.controller;

import com.wemingle.core.domain.vote.dto.VoteDto;
import com.wemingle.core.domain.vote.service.VoteService;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vote")
public class VoteController {
    private final VoteService voteService;

    @GetMapping("/completion")
    public ResponseEntity<ResponseHandler<List<VoteDto.ResponseExpiredVoteInfo>>> getExpiredVotesInfo(@RequestParam(required = false) Long nextIdx,
                                                                                                      @RequestParam Long teamPk){
        List<VoteDto.ResponseExpiredVoteInfo> responseData = voteService.getExpiredVotesInfo(nextIdx, teamPk);

        return ResponseEntity.ok(
                ResponseHandler.<List<VoteDto.ResponseExpiredVoteInfo>>builder()
                        .responseMessage("Team post votes info retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/{votePk}")
    public ResponseEntity<ResponseHandler<VoteDto.ResponseVoteResult>> getVoteResults(@PathVariable Long votePk) {
        VoteDto.ResponseVoteResult responseData = voteService.getVoteResult(votePk);

        return ResponseEntity.ok(
                ResponseHandler.<VoteDto.ResponseVoteResult>builder()
                        .responseMessage("Team post vote result retrieval successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}
