package com.wemingle.core.domain.post.dto;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.post.dto.sortoption.SortOption;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@ToString
@Setter
@Getter
public class MatchingPostMapDto {

    @Setter
    @Getter
    public static class RequestMap{
        private SortOption sortOption;
        private Long lastIdx;
        private RecruitmentType recruitmentType;
        private Ability ability;
        private Gender gender;
        private RecruiterType recruiterType;
        private List<LocalDate> dateFilter;
        private YearMonth monthFilter;
        private LocalDate lastExpiredDate;
        private Boolean excludeExpired;
        private Integer callCnt;
        private double topLat;
        private double bottomLat;
        private double leftLon;
        private double rightLon;
        private boolean excludeRegionUnit;
        private SportsType sportsType;
    }

    @Setter
    @Getter
    public static class RequestMapDetail{
        private RecruitmentType recruitmentType;
        private Ability ability;
        private Gender gender;
        private RecruiterType recruiterType;
        private List<LocalDate> dateFilter;
        private YearMonth monthFilter;
        private LocalDate lastExpiredDate;
        private Boolean excludeExpired;
        private double topLat;
        private double bottomLat;
        private double leftLon;
        private double rightLon;
        private boolean excludeRegionUnit;
        private SportsType sportsType;
    }

    @Setter
    @Getter
    public static class RequestMapCnt{

        private RecruitmentType recruitmentType;
        private Ability ability;
        private Gender gender;
        private RecruiterType recruiterType;
        private List<LocalDate> dateFilter;
        private YearMonth monthFilter;
        private LocalDate lastExpiredDate;
        private Boolean excludeExpired;
        private double topLat;
        private double bottomLat;
        private double leftLon;
        private double rightLon;
        private boolean excludeRegionUnit;
        private SportsType sportsTyp;
    }

    @Setter
    @Getter
    public static class ResponseClusterMapDetail{
        private double lat;
        private double lon;
        private int cnt;

        @Builder
        public ResponseClusterMapDetail(double lat, double lon, int cnt) {
            this.lat = lat;
            this.lon = lon;
            this.cnt = cnt;
        }
    }

}
