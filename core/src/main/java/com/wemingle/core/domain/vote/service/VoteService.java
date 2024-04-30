package com.wemingle.core.domain.vote.service;

import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.post.repository.TeamPostRepository;
import com.wemingle.core.domain.vote.dto.VoteDto;
import com.wemingle.core.domain.vote.entity.TeamPostVote;
import com.wemingle.core.domain.vote.entity.VoteOption;
import com.wemingle.core.domain.vote.entity.VoteResult;
import com.wemingle.core.domain.vote.repository.TeamPostVoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteService {
    private final TeamPostRepository teamPostRepository;
    private final TeamPostVoteRepository teamPostVoteRepository;

    public List<VoteDto.ResponseExpiredVoteInfo> getExpiredVotesInfo(Long nextIdx, Long teamPk){
        List<TeamPost> teamPosts = teamPostRepository.findByTeam_Pk(teamPk);
        List<TeamPostVote> expiredVotes = teamPostVoteRepository.getExpiredVotes(nextIdx, teamPosts);

        if (expiredVotes == null){
            return null;
        }

        return expiredVotes.stream()
                .map(vote -> VoteDto.ResponseExpiredVoteInfo.builder()
                        .teamPostVotePk(vote.getPk())
                        .title(vote.getTitle())
                        .expiryTime(vote.getExpiryTime())
                        .totalParticipantCnt(getTotalParticipantCnt(vote))
                        .voteResultInfos(getVoteResultInfos(vote.getVoteOptions()))
                        .build())
                .toList();
    }

    private long getTotalParticipantCnt(TeamPostVote teamPostVote){
        return teamPostVote.getVoteOptions().stream()
                .mapToLong(voteOption -> voteOption.getVoteResults().size())
                .sum();
    }

    private List<VoteDto.VoteResultInfo> getVoteResultInfos(List<VoteOption> voteOptions){
        return voteOptions.stream().map(voteOption -> VoteDto.VoteResultInfo.builder()
                .optionName(voteOption.getOptionName())
                .participantCnt(voteOption.getVoteResults().size())
                .teamMemberPks(getTeamMemberPks(voteOption.getVoteResults()))
                .build())
                .toList();
    }

    private static List<Long> getTeamMemberPks(List<VoteResult> voteResults) {
        return voteResults.stream()
                .map(voteResult -> voteResult.getTeamMember().getPk())
                .toList();
    }
}
