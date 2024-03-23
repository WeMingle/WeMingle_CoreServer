package com.wemingle.core.domain.post.repository;

import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.MatchingPostArea;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface DSLMatchingPostRepository {

    List<MatchingPost> findFilteredMatchingPost(Long nextIdx, RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, List<MatchingPostArea> areaList, LocalDate currentDate, Pageable pageable);

}
