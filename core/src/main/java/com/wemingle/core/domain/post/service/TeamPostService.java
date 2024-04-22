package com.wemingle.core.domain.post.service;

import com.wemingle.core.domain.bookmark.repository.BookmarkedTeamPostRepository;
import com.wemingle.core.domain.img.entity.TeamPostImg;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.post.dto.TeamPostDto;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.post.repository.TeamPostRepository;
import com.wemingle.core.domain.post.vo.SaveVoteVo;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import com.wemingle.core.domain.team.repository.TeamRepository;
import com.wemingle.core.domain.vote.entity.TeamPostVote;
import com.wemingle.core.domain.vote.entity.VoteOption;
import com.wemingle.core.domain.vote.repository.TeamPostVoteRepository;
import com.wemingle.core.domain.vote.repository.VoteOptionRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TeamPostService {
    private final TeamRepository teamRepository;
    private final TeamPostRepository teamPostRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final BookmarkedTeamPostRepository bookmarkedTeamPostRepository;
    private final S3ImgService s3ImgService;
    private final TeamPostVoteRepository teamPostVoteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final static int PAGE_SIZE = 30;

    public HashMap<Long, TeamPostDto.ResponseTeamPostsInfoWithMember> getTeamPostWithMember(Long nextIdx, String memberId){
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);
        List<Team> myTeams = teamMemberRepository.findMyTeams(memberId);
        List<TeamPost> teamPosts = teamPostRepository.getTeamPostWithMember(nextIdx, myTeams, pageRequest);
        List<TeamPost> bookmarkedTeamPosts = bookmarkedTeamPostRepository.findBookmarkedByTeamPost(teamPosts, memberId);

        HashMap<Long, TeamPostDto.ResponseTeamPostsInfoWithMember> responseData = new LinkedHashMap<>();
        teamPosts.forEach(teamPost -> responseData.put(teamPost.getPk(), TeamPostDto.ResponseTeamPostsInfoWithMember.builder()
                .teamName(teamPost.getTeam().getTeamName())
                .title(teamPost.getTitle())
                .content(teamPost.getContent())
                .nickname(teamPost.getWriter().getNickname())
                .createdTime(teamPost.getCreatedTime())
                .teamPostImgUrls(s3ImgService.getTeamPostPicUrl(getImgIds(teamPost)))
                .likeCnt(teamPost.getLikeCount())
                .replyCnt(teamPost.getReplyCount())
                .isBookmarked(isBookmarked(teamPost, bookmarkedTeamPosts))
                .voteInfo(getVoteInfo(teamPost.getTeamPostVote()))
                .build()
        ));

        return responseData;
    }

    private static List<UUID> getImgIds(TeamPost teamPost) {
        return teamPost.getTeamPostImgs().stream().map(TeamPostImg::getImgId).toList();
    }

    private boolean isBookmarked(TeamPost teamPost, List<TeamPost> bookmarkedTeamPosts) {
        return bookmarkedTeamPosts.contains(teamPost);
    }

    private TeamPostDto.VoteInfo getVoteInfo(TeamPostVote vote) {
        return TeamPostDto.VoteInfo.builder()
                .votePk(vote.getPk())
                .voteOptionInfos(vote.getVoteOptions().stream().map(voteOption -> TeamPostDto.VoteOptionInfo.builder()
                        .optionName(voteOption.getOptionName())
                        .resultCnt(voteOption.getVoteResults().size())
                        .build()).toList())
                .build();
    }

    @Transactional
    public void saveTeamPost(TeamPostDto.RequestTeamPostSave savePostDto, String memberId){
        log.info("{}", savePostDto);
        Team team = teamRepository.findById(savePostDto.getTeamPk())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_NOT_FOUND.getExceptionMessage()));
        TeamMember teamMember = teamMemberRepository.findByTeamAndMember_MemberId(team, memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));

        TeamPost teamPost = TeamPost.builder()
                .title(savePostDto.getPostTitle())
                .content(savePostDto.getPostContent())
                .commentAllow(savePostDto.isCommentAllow())
                .likeAllow(savePostDto.isLikeAllow())
                .postType(savePostDto.getPostType())
                .team(team)
                .writer(teamMember)
                .build();

        teamPost.addTeamPostImgs(createTeamPostImgs(teamPost, savePostDto.getImgIds()));

        teamPostRepository.save(teamPost);
        teamPost.addTeamPostVote(saveVote(teamPost, savePostDto.of()));
    }

    private List<TeamPostImg> createTeamPostImgs(TeamPost teamPost, List<UUID> imgIds) {
        return imgIds.stream()
                .map(img -> TeamPostImg.builder()
                        .imgId(img)
                        .teamPost(teamPost)
                        .build())
                .toList();
    }

    private TeamPostVote saveVote(TeamPost teamPost, SaveVoteVo saveVoteVo) {
        if (saveVoteVo.isHasVote()){
            log.info("isMultiVoting = {}", saveVoteVo.isMultiVoting());
            log.info("isAnonymousVoting = {}", saveVoteVo.isAnonymousVoting());
            TeamPostVote teamPostVote = TeamPostVote.builder()
                    .title(saveVoteVo.getTitle())
                    .expiryTime(saveVoteVo.getExpiryTime())
                    .isMultiVoting(saveVoteVo.isMultiVoting())
                    .isAnonymousVoting(saveVoteVo.isAnonymousVoting())
                    .voteLimit(saveVoteVo.getVoteLimit())
                    .teamPost(teamPost)
                    .build();

            List<VoteOption> voteOptions = createVoteOptions(teamPostVote, saveVoteVo.getVoteOptions());
            teamPostVote.addVoteOptions(voteOptions);

            return teamPostVoteRepository.save(teamPostVote);
        }

        return null;
    }

    private List<VoteOption> createVoteOptions(TeamPostVote teamPostVote, List<String> voteOptions) {
        return voteOptions.stream()
                .map(voteOption -> VoteOption.builder()
                        .teamPostVote(teamPostVote)
                        .optionName(voteOption)
                        .build())
                .toList();
    }
}
