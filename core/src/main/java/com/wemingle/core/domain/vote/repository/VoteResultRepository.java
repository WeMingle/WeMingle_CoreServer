package com.wemingle.core.domain.vote.repository;

import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.vote.entity.VoteOption;
import com.wemingle.core.domain.vote.entity.VoteResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteResultRepository extends JpaRepository<VoteResult, Long> {
    @Query("select vr from VoteResult vr join fetch vr.voteOption " +
            "where vr.teamMember = :teamMember and vr.voteOption in :voteOptions")
    List<VoteResult> findByTeamMemberAndVoteOptionIn(@Param("teamMember") TeamMember teamMember, @Param("voteOptions") List<VoteOption> voteOptions);
    @Query("select vr from VoteResult vr join fetch vr.voteOption join fetch vr.teamMember " +
            "where vr.voteOption in :voteOptions")
    List<VoteResult> findByVoteOptionIn(List<VoteOption> voteOptions);
}
