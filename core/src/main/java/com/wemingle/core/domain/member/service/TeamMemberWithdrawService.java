package com.wemingle.core.domain.member.service;

import com.wemingle.core.domain.bookmark.service.BookmarkService;
import com.wemingle.core.domain.comment.service.CommentService;
import com.wemingle.core.domain.comment.service.ReplyService;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.post.service.TeamPostService;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TeamMemberWithdrawService {
    private final MemberService memberService;
    private final TeamMemberService teamMemberService;
    private final S3ImgService s3ImgService;
    private final TeamPostService teamPostService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final BookmarkService bookmarkService;

    @Transactional
    public void withdrawTeamMember(Long teamMemberId) {
        Member withdrawMember = memberService.findWithdrawMember();
        TeamMember requester = teamMemberService.findById(teamMemberId);
        requester.withdraw(withdrawMember);

        s3ImgService.deleteTeamMemberProfile(requester.getProfileImg());
        teamPostService.deleteMyTeamPostLike(requester);
        deleteMyTeamPosts(requester);
        //todo 추후 신고 기능 완성 시 관련 entity 삭제
        commentService.updateCommentsWithWithdrawMember(requester);
        replyService.updateCommentsWithWithdrawMember(requester);


    }

    private void deleteMyTeamPosts(TeamMember requester) {
        List<TeamPost> myTeamPosts = teamPostService.findMyTeamPosts(requester);
        bookmarkService.deleteAllByTeamPosts(myTeamPosts);
        teamPostService.deleteAllTeamPostLike(myTeamPosts);
        commentService.deleteCommentsByTeamPosts(myTeamPosts);
    }
}
