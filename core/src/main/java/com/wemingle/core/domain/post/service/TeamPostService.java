package com.wemingle.core.domain.post.service;

import com.wemingle.core.domain.bookmark.repository.BookmarkedTeamPostRepository;
import com.wemingle.core.domain.img.entity.TeamPostImg;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.post.dto.TeamPostDto;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.post.repository.TeamPostRepository;
import com.wemingle.core.domain.post.vo.SaveVoteVo;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.entity.teamrole.TeamRole;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import com.wemingle.core.domain.team.repository.TeamRepository;
import com.wemingle.core.domain.vote.entity.TeamPostVote;
import com.wemingle.core.domain.vote.entity.VoteOption;
import com.wemingle.core.domain.vote.repository.TeamPostVoteRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TeamPostService {
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TeamPostRepository teamPostRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final BookmarkedTeamPostRepository bookmarkedTeamPostRepository;
    private final S3ImgService s3ImgService;
    private final TeamPostVoteRepository teamPostVoteRepository;

    public HashMap<Long, Object> getMyTeamPosts(Long nextIdx, Long teamId, String memberId) {
        List<TeamPost> myTeamPosts = teamPostRepository.findMyTeamPosts(nextIdx, teamId, memberId);
        LinkedHashMap<Long, Object> responseMap = new LinkedHashMap<>();
        myTeamPosts.forEach(teamPost -> responseMap.put(teamPost.getPk(),TeamPostDto.ResponseMyAllPostDto.builder()
                        .title(teamPost.getTitle())
                        .writer(teamPost.getWriter().getTeam().getTeamName())
                        .writerPic(teamPost.getWriter().getProfileImg())
                        .writeTime(teamPost.getCreatedTime())
                        .content(teamPost.getContent())
                        .picList(teamPost.getTeamPostImgs().stream().map(TeamPostImg::getImgId).toList())
                        .likeCnt(teamPost.getLikeCount())
                        .replyCnt(teamPost.getReplyCount())
                        .isBookmarked(false)
                        .voteInfo(TeamPostDto.VoteInfo.builder()
                                .votePk(teamPost.getTeamPostVote()
                                        .getPk())
                                .voteOptionInfos(teamPost.getTeamPostVote()
                                        .getVoteOptions().stream()
                                        .map(voteOption -> TeamPostDto.VoteOptionInfo.builder()
                                                .optionName(voteOption.getOptionName())
                                                .resultCnt(voteOption.getVoteResults().size())
                                                .build()
                                        ).toList()
                                ).build()
                        )
                )

        );
        return responseMap;
    }

    public HashMap<Long, TeamPostDto.ResponseTeamPostsInfoWithMember> getTeamPostWithMember(Long nextIdx, String memberId){
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.MEMBER_NOT_FOUNT.getExceptionMessage()));
        List<Team> myTeams = teamMemberRepository.findMyTeams(memberId);
        List<TeamPost> teamPosts = teamPostRepository.getTeamPostWithMember(nextIdx, myTeams);

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
                .isWriter(isWriter(teamPost, member))
                .isBookmarked(isBookmarked(teamPost, bookmarkedTeamPosts))
                .voteInfo(getVoteInfo(teamPost.getTeamPostVote()))
                .build()
        ));
        return responseData;
    }

    private boolean isWriter(TeamPost teamPost, Member member) {
        return teamPost.getWriter().getMember().equals(member);
    }

    public TeamPostDto.ResponseTeamPostsInfoWithTeam getTeamPostWithTeam(Long nextIdx, boolean isNotice, Long teamPk, String memberId){
        Team team = teamRepository.findById(teamPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_NOT_FOUND.getExceptionMessage()));
        Optional<TeamMember> teamMember = teamMemberRepository.findByTeamAndMember_MemberId(team, memberId);
        List<TeamPost> teamPosts = teamPostRepository.getTeamPostWithTeam(nextIdx, team ,isNotice);

        List<TeamPost> bookmarkedTeamPosts = bookmarkedTeamPostRepository.findBookmarkedByTeamPost(teamPosts, memberId);

        HashMap<Long, TeamPostDto.TeamPostInfo> responseData = new LinkedHashMap<>();

        teamPosts.forEach(teamPost -> responseData.put(teamPost.getPk(), TeamPostDto.TeamPostInfo.builder()
                .title(teamPost.getTitle())
                .content(teamPost.getContent())
                .nickname(teamPost.getWriter().getNickname())
                .createdTime(teamPost.getCreatedTime())
                .teamPostImgUrls(s3ImgService.getTeamPostPicUrl(getImgIds(teamPost)))
                .likeCnt(teamPost.getLikeCount())
                .replyCnt(teamPost.getReplyCount())
                .postType(teamPost.getPostType())
                .isWriter(isWriter(teamPost, teamMember))
                .isBookmarked(isBookmarked(teamPost, bookmarkedTeamPosts))
                .voteInfo(getVoteInfo(teamPost.getTeamPostVote()))
                .build()
        ));

        return TeamPostDto.ResponseTeamPostsInfoWithTeam.builder()
                .teamName(team.getTeamName())
                .hasWritePermission(isTeamOwner(teamMember))
                .teamPostsInfo(responseData)
                .build();
    }

    private List<UUID> getImgIds(TeamPost teamPost) {
        return teamPost.getTeamPostImgs().stream().map(TeamPostImg::getImgId).toList();
    }

    private boolean isWriter(TeamPost teamPost, Optional<TeamMember> teamMember) {
        return teamMember.isPresent() ? teamPost.getWriter().equals(teamMember.get()) : false;
    }

    private boolean isBookmarked(TeamPost teamPost, List<TeamPost> bookmarkedTeamPosts) {
        return bookmarkedTeamPosts.contains(teamPost);
    }

    private TeamPostDto.VoteInfo getVoteInfo(TeamPostVote vote) {
        if (vote == null){
            return null;
        }

        return TeamPostDto.VoteInfo.builder()
                .votePk(vote.getPk())
                .voteOptionInfos(vote.getVoteOptions().stream().map(voteOption -> TeamPostDto.VoteOptionInfo.builder()
                        .optionName(voteOption.getOptionName())
                        .resultCnt(voteOption.getVoteResults().size())
                        .build()).toList())
                .build();
    }

    private boolean isTeamOwner(Optional<TeamMember> teamMember) {
        return teamMember.isPresent() ? !teamMember.get().getTeamRole().equals(TeamRole.PARTICIPANT) : false;
    }

    @Transactional
    public void saveTeamPost(TeamPostDto.RequestTeamPostSave savePostDto, String memberId){
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


    public HashMap<Long, TeamPostDto.ResponseSearchTeamPost> getSearchTeamPost(Long nextIdx, Long teamPk, String searchWord, String memberId){
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.MEMBER_NOT_FOUNT.getExceptionMessage()));
        Team team = teamRepository.findById(teamPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_NOT_FOUND.getExceptionMessage()));

        List<TeamPost> myBookmarkedTeamPosts = bookmarkedTeamPostRepository.findTeamPostByMember(member);
        List<TeamPost> searchTeamPosts = teamPostRepository.getSearchTeamPost(nextIdx, team, searchWord);
        LinkedHashMap<Long, TeamPostDto.ResponseSearchTeamPost> responseData = new LinkedHashMap<>();

        searchTeamPosts.forEach(teampost -> responseData.put(teampost.getPk(), TeamPostDto.ResponseSearchTeamPost
                .builder()
                .title(teampost.getTitle())
                .content(teampost.getContent())
                .writerName(teampost.getWriter().getNickname())
                .createTime(teampost.getCreatedTime())
                .likeCnt(teampost.getLikeCount())
                .replyCnt(teampost.getReplyCount())
                .isBookmarked(isBookmarked(teampost, myBookmarkedTeamPosts))
                .isWriter(isWriter(teampost, member))
                .build()));

        return responseData;
    }
}
