package com.wemingle.core.domain.comment.service;

import com.wemingle.core.domain.comment.dto.ReplyDto;
import com.wemingle.core.domain.comment.entity.Comment;
import com.wemingle.core.domain.comment.entity.Reply;
import com.wemingle.core.domain.comment.repository.CommentRepository;
import com.wemingle.core.domain.comment.repository.ReplyRepository;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.post.service.TeamPostService;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.service.TeamMemberService;
import com.wemingle.core.global.exception.NotWriterException;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import com.wemingle.core.global.util.CommentResponseUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {
    private final CommentRepository commentRepository;
    private final TeamPostService teamPostService;
    private final ReplyRepository replyRepository;
    private final S3ImgService s3ImgService;
    private final TeamMemberService teamMemberService;

    @Value("${wemingle.ip}")
    private String serverIp;

    @Transactional
    public void saveReply(ReplyDto.RequestReplySave saveDto, String memberId){
        Comment comment = commentRepository.findByIdFetchPost(saveDto.getCommentId())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.COMMENT_NOT_FOUND.getExceptionMessage()));
        TeamMember requester = teamMemberService.findByTeamAndMember_MemberId(comment.getTeamPost().getTeam(), memberId);

        replyRepository.save(Reply.builder()
                .comment(comment)
                .writer(requester)
                .content(saveDto.getContent())
                .build());

        comment.getTeamPost().addReplyCnt();
    }

    public boolean isReplyWriter(Long replyPk, String memberId){
        Reply reply = findById(replyPk);
        TeamMember requester = teamMemberService.findByTeamAndMember_MemberId(reply.getWriter().getTeam(), memberId);

        return reply.isWriter(requester);
    }

    @Transactional
    public void updateReply(ReplyDto.RequestReplyUpdate updateDto, String memberId) {
        if (!isReplyWriter(updateDto.getReplyId(), memberId)){
            throw new NotWriterException();
        }
        Reply reply = findById(updateDto.getReplyId());

        reply.updateContent(updateDto.getContent());
    }

    @Transactional
    public void deleteReply(ReplyDto.RequestReplyDelete deleteDto, String memberId) {
        if (!isReplyWriter(deleteDto.getReplyId(), memberId)){
            throw new NotWriterException();
        }

        Reply reply = findById(deleteDto.getReplyId());
        TeamPost teamPost = teamPostService.findById(deleteDto.getTeamPostId());

        reply.delete();
        teamPost.reduceReplyCnt();
    }

    public ReplyDto.ResponseRepliesRetrieve getReplies(Long nextIdx, Long commentId, String memberId){
        List<Reply> replies = replyRepository.findRepliesByNextIdx(nextIdx, commentId);
        Team team = commentRepository.findTeam(commentId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_NOT_FOUND.getExceptionMessage()));
        TeamMember requester = teamMemberService.findByTeamAndMember_MemberId(team, memberId);

        return createResponseReplies(commentId, replies, requester);
    }

    public ReplyDto.ResponseRepliesRetrieve createResponseReplies(Long commentPk, List<Reply> replies, TeamMember requester) {
        CommentResponseUtil<Reply> commentResponseUtil = new CommentResponseUtil<>(serverIp);
        String nextUrl = commentResponseUtil.createRepliesNextUrl(replies, commentPk);
        List<Reply> filteredReplies = commentResponseUtil.removeLastDataIfExceedNextDataMarker(replies);

        LinkedHashMap<Long, ReplyDto.ReplyInfo> repliesInfo = new LinkedHashMap<>();
        filteredReplies.forEach(reply -> repliesInfo.put(reply.getPk(), ReplyDto.ReplyInfo.builder()
                .imgUrl(s3ImgService.getTeamMemberPreSignedUrl(reply.getWriter().getProfileImg()))
                .nickname(reply.getWriter().getNickname())
                .content(reply.getContent())
                .createDate(reply.getCreatedTime().toLocalDate())
                .isWriter(reply.isWriter(requester))
                .isDeleted(reply.isDeleted())
                .isLocked(reply.isLocked())
                .build()));

        return ReplyDto.ResponseRepliesRetrieve.builder()
                .repliesInfo(repliesInfo)
                .nextUrl(nextUrl)
                .build();
    }

    public List<Reply> findRankRepliesByComments(List<Long> commentIds){
        return replyRepository.findRankRepliesByComments(commentIds);
    }

    public Reply findById(Long replyId) {
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.REPLY_NOT_FOUND.getExceptionMessage()));
    }

    @Transactional
    public void updateRepliesWithWithdrawMember(TeamMember withdrawMember) {
        List<Reply> myReplies = replyRepository.findByWriter(withdrawMember);
        myReplies.forEach(Reply::updateByWithdrawMember);
    }

    public void deleteAllByComments(List<Comment> comments) {
        List<Reply> replies = replyRepository.findByCommentIn(comments);
        replyRepository.deleteAllInBatch(replies);
    }
}
