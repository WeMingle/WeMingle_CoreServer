package com.wemingle.core.domain.post.service;

import com.wemingle.core.domain.bookmark.repository.BookmarkedTeamPostRepository;
import com.wemingle.core.domain.img.entity.TeamPostImg;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.post.dto.TeamPostDto;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.post.repository.TeamPostRepository;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import com.wemingle.core.domain.vote.entity.TeamPostVote;
import lombok.RequiredArgsConstructor;
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
public class TeamPostService {
    private final TeamPostRepository teamPostRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final BookmarkedTeamPostRepository bookmarkedTeamPostRepository;
    private final S3ImgService s3ImgService;
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
}
