package com.wemingle.core.domain.post.repository;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.dto.sortoption.SortOption;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface DSLMatchingPostRepository {

    List<MatchingPost> findFilteredMatchingPost(Long lastIdx, RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, List<AreaName> areaList, LocalDate currentDate, LocalDate dateFilter, YearMonth monthFilter, SortOption sortOption, Long lastViewCnt, LocalDate lastExpiredDate, Pageable pageable);
    Integer findFilteredMatchingPostCnt(Long lastIdx, RecruitmentType recruitmentType, Ability ability, Gender gender, RecruiterType recruiterType, List<AreaName> areaList, LocalDate currentDate, LocalDate dateFilter, YearMonth monthFilter, Pageable pageable);
    List<MatchingPost> findCompletedMatchingPosts(Long nextIdx, RecruiterType recruiterType, boolean excludeCompleteMatchesFilter, Member member, List<MatchingPost> matchingPostWithReview, Pageable pageable);
    List<MatchingPost> findMatchingPostInMap(double topLat, double bottomLat, double leftLon, double rightLon);
}
