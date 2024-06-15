package com.wemingle.core.domain.member.service;

import com.wemingle.core.domain.bookmark.service.BookmarkService;
import com.wemingle.core.domain.comment.entity.Comment;
import com.wemingle.core.domain.comment.service.CommentService;
import com.wemingle.core.domain.comment.service.ReplyService;
import com.wemingle.core.domain.img.entity.TeamPostImg;
import com.wemingle.core.domain.img.repository.TeamPostImgRepository;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.team.dto.TeamMemberDto;
import com.wemingle.core.domain.member.entity.BannedTeamMember;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.BannedTeamMemberRepository;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.post.repository.TeamPostRepository;
import com.wemingle.core.domain.post.service.TeamPostService;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.service.TeamMemberService;
import com.wemingle.core.domain.vote.service.VoteService;
import com.wemingle.core.global.exception.NotManagerException;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TeamMemberLeaveService {
    private final MemberService memberService;
    private final TeamMemberService teamMemberService;
    private final S3ImgService s3ImgService;
    private final TeamPostService teamPostService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final BookmarkService bookmarkService;
    private final TeamPostRepository teamPostRepository;
    private final TeamPostImgRepository teamPostImgRepository;
    private final VoteService voteService;
    private final BannedTeamMemberRepository bannedTeamMemberRepository;

    @Transactional
    public void withdrawTeamMember(Long teamMemberId, String memberId) {
        TeamMember requester = teamMemberService.findById(teamMemberId);
        if (!requester.getMember().getMemberId().equals(memberId)) {
            throw new RuntimeException(ExceptionMessage.IS_NOT_ME.getExceptionMessage());
        }

        Member withdrawMember = memberService.findWithdrawMember();

        s3ImgService.deleteTeamMemberProfile(requester.getProfileImg());
        teamPostService.deleteMyTeamPostLike(requester);
        bookmarkService.deleteByTeamAndMember(requester.getTeam(), requester.getMember());
        deleteMyTeamPosts(requester);
        //todo 추후 신고 기능 완성 시 관련 entity 삭제
        commentService.updateCommentsWithWithdrawMember(requester);
        replyService.updateRepliesWithWithdrawMember(requester);
        requester.withdraw(withdrawMember);
    }

    private void deleteMyTeamPosts(TeamMember requester) {
        List<TeamPost> myTeamPosts = teamPostService.findMyTeamPosts(requester);
        bookmarkService.deleteAllByTeamPosts(myTeamPosts);
        teamPostService.deleteAllTeamPostLike(myTeamPosts);
        List<Comment> allComments = commentService.getAllCommentsByTeamPostIn(myTeamPosts);
        //todo 글, 댓글, 대댓글 신고 entity 삭제
        replyService.deleteAllByComments(allComments);
        commentService.deleteCommentsByTeamPosts(myTeamPosts);
        List<TeamPostImg> allTeamPostImgs = teamPostImgRepository.findByTeamPostIn(myTeamPosts);
        teamPostImgRepository.deleteAllInBatch(allTeamPostImgs);
        voteService.deleteAllTeamPostVote(myTeamPosts);
        teamPostRepository.deleteAllInBatch(myTeamPosts);
    }

    @Transactional
    public void banTeamMember(TeamMemberDto.RequestTeamMemberBan banDto) {
        TeamMember requester = teamMemberService.findById(banDto.getRequesterId());
        if (!requester.isManager()) {
            throw new NotManagerException();
        }

        TeamMember target = teamMemberService.findById(banDto.getTargetId());
        bannedTeamMemberRepository.save(BannedTeamMember.builder()
                .team(target.getTeam())
                .bannedMember(target.getMember())
                .build());

        withdrawTeamMember(banDto.getTargetId(), target.getMember().getMemberId());
    }
}
