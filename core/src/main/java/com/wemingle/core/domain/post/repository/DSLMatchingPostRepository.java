package com.wemingle.core.domain.post.repository;

import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface DSLMatchingPostRepository {

    List<MatchingPost> findFilteredMatchingPost(Long nextIdx, RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, List<AreaName> areaList, LocalDate currentDate, LocalDate dateFilter, Pageable pageable);
    List<MatchingPost> findFilteredMatchingPostInMatchingFeed(Long nextIdx, RecruiterType recruiterType, boolean completeMatchesFilter, Pageable pageable);

}
