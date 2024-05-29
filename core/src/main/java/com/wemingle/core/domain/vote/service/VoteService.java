package com.wemingle.core.domain.vote.service;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.post.repository.TeamPostRepository;
import com.wemingle.core.domain.vote.dto.VoteDto;
import com.wemingle.core.domain.vote.entity.TeamPostVote;
import com.wemingle.core.domain.vote.entity.VoteOption;
import com.wemingle.core.domain.vote.entity.VoteResult;
import com.wemingle.core.domain.vote.repository.TeamPostVoteRepository;
import com.wemingle.core.domain.vote.repository.VoteResultRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
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
    private final VoteResultRepository voteResultRepository;
    private final S3ImgService s3ImgService;

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

    private List<Long> getTeamMemberPks(List<VoteResult> voteResults) {
        return voteResults.stream()
                .map(voteResult -> voteResult.getTeamMember().getPk())
                .toList();
    }

    public VoteDto.ResponseVoteResult getVoteResult(Long teamPostVotePk) {
        TeamPostVote teamPostVote = teamPostVoteRepository.findById(teamPostVotePk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.VOTE_NOT_FOUND.getExceptionMessage()));

        return VoteDto.ResponseVoteResult.builder()
                .title(teamPostVote.getTitle())
                .expiryTime(teamPostVote.getExpiryTime())
                .voteOptionResults(getVoteOptionResult(teamPostVote))
                .build();
    }

    private List<VoteDto.VoteOptionResult> getVoteOptionResult(TeamPostVote teamPostVote) {
        List<VoteOption> distinctVoteOptions = teamPostVote.getVoteOptions();
        List<VoteResult> voteResults = voteResultRepository.findByVoteOptionIn(distinctVoteOptions);
//        List<VoteOption> distinctVoteOptions = voteResults.stream().map(VoteResult::getVoteOption).distinct().toList();

        return distinctVoteOptions.stream()
                .map(distinctVoteOption -> VoteDto.VoteOptionResult
                        .builder()
                        .optionName(distinctVoteOption.getOptionName())
                        .totalCnt(getTotalCnt(distinctVoteOption, voteResults))
                        .teamMemberInfo(getTeamMembersInfo(distinctVoteOption, voteResults)
                        )
                        .build())
                .toList();
    }

    private long getTotalCnt(VoteOption voteOptionCategory, List<VoteResult> voteResults) {
        return voteResults.stream()
                .filter(voteResult -> voteResult.getVoteOption().equals(voteOptionCategory))
                .count();
    }

    private List<VoteDto.TeamMemberInfo> getTeamMembersInfo(VoteOption voteOptionCategory, List<VoteResult> voteResults) {
        return voteResults.stream()
                .filter(voteResult -> voteResult.getVoteOption().equals(voteOptionCategory))
                .map(voteResult -> VoteDto.TeamMemberInfo.builder()
                        .nickname(voteResult.getTeamMember().getNickname())
                        .imgUrl(s3ImgService.getTeamMemberPreSignedUrl(voteResult.getTeamMember().getProfileImg()))
                        .build())
                .toList();
    }
}
