package com.wemingle.core.domain.post.repository;

import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatchingPostRepository extends JpaRepository<MatchingPost,Long> {
//    @Query("select m from MatchingPost m join fetch m.team join fetch m.writer w join fetch w.team where " +
//            "(:nextIdx is null or m.pk < :nextIdx) and " +
//            "(:recruitmentType is null or m.recruitmentType = :recruitmentType) and " +
//            "(:ability is null or m.ability = :ability) and " +
//            "(:gender is null or m.gender = :gender) and " +
//            "(:recruiterType IS NULL OR m.recruiterType = :recruiterType) and " +
//            "(:areaName is null or m.areaName = :areaName) and " +
//            "(:currentDate is null or m.expiryDate <= :currentDate) " +
//            "order by m.pk desc ")
//    List<MatchingPost> findFilteredMatchingPost(@Param("nextIdx") Long nextIdx,
//                                                @Param("recruitmentType") String recruitmentType,
//                                                @Param("ability") String ability,
//                                                @Param("gender") String gender,
//                                                @Param("recruiterType") String recruiterType,
//                                                @Param("areaName") String areaName,
//                                                @Param("currentDate") LocalDate currentDate,
//                                                Pageable pageable);

    Optional<MatchingPost> findByWriter(TeamMember writer);
}
