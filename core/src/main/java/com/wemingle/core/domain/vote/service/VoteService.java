package com.wemingle.core.domain.vote.service;

import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.post.repository.TeamPostRepository;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.service.TeamMemberService;
import com.wemingle.core.domain.vote.dto.VoteDto;
import com.wemingle.core.domain.vote.entity.TeamPostVote;
import com.wemingle.core.domain.vote.entity.VoteOption;
import com.wemingle.core.domain.vote.entity.VoteResult;
import com.wemingle.core.domain.vote.repository.TeamPostVoteRepository;
import com.wemingle.core.domain.vote.repository.VoteOptionRepository;
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
    private final VoteOptionRepository voteOptionRepository;
    private final S3ImgService s3ImgService;
    private final TeamMemberService teamMemberService;

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

    public VoteDto.ResponseVoteResult getVoteResult(Long teamPostVoteId) {
        TeamPostVote teamPostVote = findById(teamPostVoteId);

        return VoteDto.ResponseVoteResult.builder()
                .title(teamPostVote.getTitle())
                .expiryTime(teamPostVote.getExpiryTime())
                .isAnonymousVoting(teamPostVote.isAnonymousVoting())
                .voteOptionResults(getVoteOptionResult(teamPostVote))
                .build();
    }

    private List<VoteDto.VoteOptionResult> getVoteOptionResult(TeamPostVote teamPostVote) {
        List<VoteOption> distinctVoteOptions = teamPostVote.getVoteOptions();
        List<VoteResult> voteResults = voteResultRepository.findByVoteOptionIn(distinctVoteOptions);

        return distinctVoteOptions.stream()
                .map(distinctVoteOption -> VoteDto.VoteOptionResult
                        .builder()
                        .optionName(distinctVoteOption.getOptionName())
                        .totalCnt(getTotalCntWithOption(distinctVoteOption, voteResults))
                        .teamMemberInfo(getTeamMembersInfo(distinctVoteOption, voteResults, teamPostVote.isAnonymousVoting())
                        )
                        .build())
                .toList();
    }

    private long getTotalCntWithOption(VoteOption voteOptionCategory, List<VoteResult> voteResults) {
        return voteResults.stream()
                .filter(voteResult -> voteResult.getVoteOption().equals(voteOptionCategory))
                .count();
    }

    private List<VoteDto.TeamMemberInfo> getTeamMembersInfo(VoteOption voteOptionCategory, List<VoteResult> voteResults, boolean isAnonymousVoting) {
        if (isAnonymousVoting) {
            return null;
        }

        return voteResults.stream()
                .filter(voteResult -> voteResult.getVoteOption().equals(voteOptionCategory))
                .map(voteResult -> VoteDto.TeamMemberInfo.builder()
                        .teamMemberPk(voteResult.getTeamMember().getPk())
                        .nickname(voteResult.getTeamMember().getNickname())
                        .imgUrl(s3ImgService.getTeamMemberPreSignedUrl(voteResult.getTeamMember().getProfileImg()))
                        .build())
                .toList();
    }

    public boolean isExceedVoteLimitWhenFirstServedBased(VoteDto.RequestVote voteDto) {
        TeamPostVote teamPostVote = findById(voteDto.getVotePk());

        return teamPostVote.isFirstServedBasedVote() && getTotalCnt(teamPostVote) + voteDto.calculateTotalCnt() > teamPostVote.getVoteLimit();
    }
    @Transactional
    public void saveOrDeleteVoteResult(VoteDto.RequestVote voteDto, String memberId) {
        if (voteDto.getRemoveVoteResult() != null && !voteDto.getRemoveVoteResult().isEmpty()) {
            List<VoteResult> removeVoteResults = voteResultRepository.findAllById(voteDto.getRemoveVoteResult());
            removeVoteResults.forEach(removeVoteResult -> removeVoteResult.getVoteOption().removeVoteResult(removeVoteResult));
        }

        if (voteDto.getSaveVoteResult() != null && !voteDto.getSaveVoteResult().isEmpty()) {
            TeamPostVote teamPostVote = findById(voteDto.getVotePk());
            TeamMember requester = teamMemberService.findByTeamAndMember_MemberId(teamPostVote.getTeamPost().getTeam(), memberId);
            List<VoteOption> saveVotes = voteOptionRepository.findAllById(voteDto.getSaveVoteResult());

            List<VoteResult> saveVoteResults = saveVotes.stream()
                    .map(saveVote -> VoteResult.builder()
                            .voteOption(saveVote)
                            .teamMember(requester)
                            .build())
                    .toList();

            saveVoteResults.forEach(saveVoteResult -> saveVoteResult.getVoteOption().addVoteResult(saveVoteResult)); //voteResult 저장

            if (isReachVoteLimit(teamPostVote)) {
                teamPostVote.complete();
            }
        }
    }

    private boolean isReachVoteLimit(TeamPostVote teamPostVote) {
        return teamPostVote.isFirstServedBasedVote() &&
                getTotalCnt(teamPostVote) == teamPostVote.getVoteLimit();
    }

    private int getTotalCnt(TeamPostVote teamPostVote) {
        return teamPostVote.getVoteOptions().stream()
                .mapToInt(voteOption -> voteOption.getVoteResults().size())
                .sum();
    }

    @Transactional
    public void completeVote(Long voteId) {
        TeamPostVote teamPostVote = findById(voteId);

        teamPostVote.complete();
    }

    public TeamPostVote findById(Long voteId) {
        return teamPostVoteRepository.findById(voteId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.VOTE_NOT_FOUND.getExceptionMessage()));
    }
}
