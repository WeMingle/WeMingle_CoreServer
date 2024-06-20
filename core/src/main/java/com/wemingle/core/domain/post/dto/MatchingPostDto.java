package com.wemingle.core.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.post.dto.sortoption.SortOption;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.MatchingPostArea;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.locationselectiontype.LocationSelectionType;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class MatchingPostDto {

    @Setter
    @Getter
    public static class RequestCalenderCntDto{
        private RecruitmentType recruitmentType;
        private Ability ability;
        private Gender gender;
        private RecruiterType recruiterType;
        private List<AreaName> areaList;
        private LocalDate dateFilter;
        private YearMonth monthFilter;
        private Boolean excludeExpired;
        private SportsType sportsTyp;
    }

    @Setter
    @Getter
    public static class RequestCalendarDto{
        private SortOption sortOption;
        private Long lastIdx;
        private RecruitmentType recruitmentType;
        private Ability ability;
        private Gender gender;
        private RecruiterType recruiterType;
        private List<AreaName> areaList;
        private LocalDate dateFilter;
        private YearMonth monthFilter;
        private LocalDate lastExpiredDate;
        private Boolean excludeExpired;
        private Integer callCnt;
        private SportsType sportsType;
    }
    @Setter
    @Getter
    public static class ResponseMyBookmarkDto {
        private Long pk;
        private String profilePicUrl;
        private String writer;
        private String contents;
        private List<AreaName> areaList;
        private int matchingCnt;
        private LocalDate matchingDate;
        private RecruiterType recruiterType;
        private Ability ability;
        private boolean isLocationConsensusPossible;
        private boolean isBookmarked;

        @Builder
        public ResponseMyBookmarkDto(Long pk, String profilePicUrl, String writer, String contents, List<AreaName> areaList, int matchingCnt, LocalDate matchingDate, RecruiterType recruiterType, Ability ability, boolean isLocationConsensusPossible, boolean isBookmarked) {
            this.pk = pk;
            this.profilePicUrl = profilePicUrl;
            this.writer = writer;
            this.contents = contents;
            this.areaList = areaList;
            this.matchingCnt = matchingCnt;
            this.matchingDate = matchingDate;
            this.recruiterType = recruiterType;
            this.ability = ability;
            this.isLocationConsensusPossible = isLocationConsensusPossible;
            this.isBookmarked = isBookmarked;
        }
    }

    @ToString
    @Setter
    @Getter
    public static class ResponseMatchingPostDto {
        private String profilePicUrl;
        private String writer;
        private String contents;
        private List<String> areaList;
        private int matchingCnt;
        private int viewCnt;
        private LocalDate matchingDate;
        private MatchingStatus matchingStatus;
        private LocalDate expiryDate;
        private RecruiterType recruiterType;
        private Ability ability;
        private boolean isLocationConsensusPossible;
        private boolean isBookmarked;

        @Builder
        public ResponseMatchingPostDto(String profilePicUrl, String writer, String contents, List<String> areaList, int matchingCnt, int viewCnt, LocalDate matchingDate, LocalDate expiryDate, RecruiterType recruiterType, Ability ability, boolean isLocationConsensusPossible, boolean isBookmarked, MatchingStatus matchingStatus) {
            this.profilePicUrl = profilePicUrl;
            this.writer = writer;
            this.contents = contents;
            this.areaList = areaList;
            this.matchingCnt = matchingCnt;
            this.viewCnt = viewCnt;
            this.matchingDate = matchingDate;
            this.expiryDate = expiryDate;
            this.recruiterType = recruiterType;
            this.ability = ability;
            this.isLocationConsensusPossible = isLocationConsensusPossible;
            this.isBookmarked = isBookmarked;
            this.matchingStatus = matchingStatus;
        }
    }

    @Setter
    @Getter
    public static class ResponseMatchingPostByMapDetailDto {
        private String profilePicUrl;
        private String writer;
        private String contents;
        private List<AreaName> areaList;
        private int matchingCnt;
        private int viewCnt;
        private LocalDate matchingDate;
        private LocalDate expiryDate;
        private RecruiterType recruiterType;
        private Ability ability;
        private boolean isLocationConsensusPossible;
        private boolean isBookmarked;
        private Double lat;
        private Double lon;
        private boolean isMatchedBefore;


        @Builder
        public ResponseMatchingPostByMapDetailDto(String profilePicUrl, String writer, String contents, List<AreaName> areaList, int matchingCnt, int viewCnt, LocalDate matchingDate, LocalDate expiryDate, RecruiterType recruiterType, Ability ability, boolean isLocationConsensusPossible, boolean isBookmarked, Double lat, Double lon, boolean isMatchedBefore) {
            this.profilePicUrl = profilePicUrl;
            this.writer = writer;
            this.contents = contents;
            this.areaList = areaList;
            this.matchingCnt = matchingCnt;
            this.viewCnt = viewCnt;
            this.matchingDate = matchingDate;
            this.expiryDate = expiryDate;
            this.recruiterType = recruiterType;
            this.ability = ability;
            this.isLocationConsensusPossible = isLocationConsensusPossible;
            this.isBookmarked = isBookmarked;
            this.lat = lat;
            this.lon = lon;
            this.isMatchedBefore = isMatchedBefore;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateMatchingPostDto {
        private LocalDate matchingDate;
        private Double latitude;
        private Double longitude;
        @NotBlank
        private String locationName;
        private String dou;
        private String si;
        private String gun;
        private String gu;
        private String dong;
        private String eup;
        private String myeon;
        private String ri;
        private List<AreaName> areaNameList;
        @JsonProperty(value = "isLocationConsensusPossible")
        private boolean isLocationConsensusPossible;
        private Ability ability;
        private Gender gender;
        @Min(value = 1, message = "myCapacityCount must be greater than 1")
        private int myCapacityCount;
        @Min(value = 1, message = "capacityLimit must be greater than 1")
        private int capacityLimit;
        private Long teamPk;
        private List<Long> participantsId = new ArrayList<>(); // member pk
        private LocalDate expiryDate;
        private RecruiterType recruiterType;
        private RecruitmentType recruitmentType;
        @NotBlank
        private String content;
        private LocationSelectionType locationSelectionType;
        private SportsType sportsType;

        @Builder
        public CreateMatchingPostDto(LocalDate matchingDate, Double latitude, Double longitude, String locationName, String dou, String si, String gun, String gu, String dong, String eup, String myeon, String ri, List<AreaName> areaNameList, boolean isLocationConsensusPossible, Ability ability, Gender gender, int myCapacityCount, int capacityLimit, Long teamPk, List<Long> participantsId, LocalDate expiryDate, RecruiterType recruiterType, RecruitmentType recruitmentType, String content, LocationSelectionType locationSelectionType, SportsType sportsType) {
            this.matchingDate = matchingDate;
            this.latitude = latitude;
            this.longitude = longitude;
            this.locationName = locationName;
            this.dou = dou;
            this.si = si;
            this.gun = gun;
            this.gu = gu;
            this.dong = dong;
            this.eup = eup;
            this.myeon = myeon;
            this.ri = ri;
            this.areaNameList = areaNameList;
            this.isLocationConsensusPossible = isLocationConsensusPossible;
            this.ability = ability;
            this.gender = gender;
            this.myCapacityCount = myCapacityCount;
            this.capacityLimit = capacityLimit;
            this.teamPk = teamPk;
            this.participantsId = participantsId;
            this.expiryDate = expiryDate;
            this.recruiterType = recruiterType;
            this.recruitmentType = recruitmentType;
            this.content = content;
            this.locationSelectionType = locationSelectionType;
            this.sportsType = sportsType;
        }

        public MatchingPost of(Team team, TeamMember writer, LocalDate matchingDate) {
            MatchingPost matchingPost = MatchingPost.builder()
                    .expiryDate(expiryDate)
                    .matchingDate(matchingDate)
                    .locationName(locationName)
                    .dou(dou)
                    .si(si)
                    .gun(gun)
                    .gu(gu)
                    .dong(dong)
                    .eup(eup)
                    .myeon(myeon)
                    .ri(ri)
                    .lat(latitude)
                    .lon(longitude)
                    .content(content)
                    .myCapacityCount(myCapacityCount)
                    .capacityLimit(capacityLimit)
                    .isLocationConsensusPossible(isLocationConsensusPossible)
                    .ability(ability)
                    .gender(gender)
                    .recruiterType(recruiterType)
                    .recruitmentType(recruitmentType)
                    .sportsCategory(sportsType)
                    .locationSelectionType(locationSelectionType)
                    .writer(writer)
                    .team(team)
                    .build();

            List<MatchingPostArea> areaList = areaNameList.stream().map(areaName -> new MatchingPostArea(areaName, matchingPost)).toList();
            matchingPost.putAreaList(areaList);

            return matchingPost;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseCompletedMatchingPost {
        private LocalDate matchingDate;
        private RecruiterType recruiterType;
        private String nickname;
        private int completedMatchingCnt;
        private String content;
        private List<String> areaNames;
        private boolean isLocationConsensusPossible;
        private Ability ability;
        private String profileImgUrl;
        private String matchingStatus;
        private String scheduledRequestDescription;

        @Builder
        public ResponseCompletedMatchingPost(LocalDate matchingDate, RecruiterType recruiterType, String nickname, int completedMatchingCnt, String content, List<String> areaNames, boolean isLocationConsensusPossible, Ability ability, String profileImgUrl, String matchingStatus, String scheduledRequestDescription) {
            this.matchingDate = matchingDate;
            this.recruiterType = recruiterType;
            this.nickname = nickname;
            this.completedMatchingCnt = completedMatchingCnt;
            this.content = content;
            this.areaNames = areaNames;
            this.isLocationConsensusPossible = isLocationConsensusPossible;
            this.ability = ability;
            this.profileImgUrl = profileImgUrl;
            this.matchingStatus = matchingStatus;
            this.scheduledRequestDescription = scheduledRequestDescription;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestComplete {
        @NotNull
        private Long matchingPostPk;

        @Builder
        public RequestComplete(Long matchingPostPk) {
            this.matchingPostPk = matchingPostPk;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseTop15PopularPost {
        private String imgUrl;
        private String nickname;
        private List<String> areas;
        private int matchingCnt;
        private LocalDate matchingDate;
        private LocalDate expiryDate;
        private RecruiterType recruiterType;
        private Ability ability;
        @JsonProperty(value = "isLocationConsensusPossible")
        private boolean isLocationConsensusPossible;

        @Builder
        public ResponseTop15PopularPost(String imgUrl, String nickname, List<String> areas, int matchingCnt, LocalDate matchingDate, LocalDate expiryDate, RecruiterType recruiterType, Ability ability, boolean isLocationConsensusPossible) {
            this.imgUrl = imgUrl;
            this.nickname = nickname;
            this.areas = areas;
            this.matchingCnt = matchingCnt;
            this.matchingDate = matchingDate;
            this.expiryDate = expiryDate;
            this.recruiterType = recruiterType;
            this.ability = ability;
            this.isLocationConsensusPossible = isLocationConsensusPossible;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseTop200PopularPost {
        private String imgUrl;
        private String nickname;
        private String content;
        private List<String> areas;
        private int matchingCnt;
        private LocalDate matchingDate;
        private RecruiterType recruiterType;
        private Ability ability;
        @JsonProperty(value = "isLocationConsensusPossible")
        private boolean isLocationConsensusPossible;
        @JsonProperty(value = "isBookmarked")
        private boolean isBookmarked;
        @JsonProperty(value = "isExpired")
        private boolean isExpired;

        @Builder
        public ResponseTop200PopularPost(String imgUrl, String nickname, String content, List<String> areas, int matchingCnt, LocalDate matchingDate, RecruiterType recruiterType, Ability ability, boolean isLocationConsensusPossible, boolean isBookmarked, boolean isExpired) {
            this.imgUrl = imgUrl;
            this.nickname = nickname;
            this.content = content;
            this.areas = areas;
            this.matchingCnt = matchingCnt;
            this.matchingDate = matchingDate;
            this.recruiterType = recruiterType;
            this.ability = ability;
            this.isLocationConsensusPossible = isLocationConsensusPossible;
            this.isBookmarked = isBookmarked;
            this.isExpired = isExpired;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseRecentPost {
        private String imgUrl;
        private String nickname;
        private String content;
        private List<String> areas;
        private int matchingCnt;
        private LocalDate matchingDate;
        private RecruiterType recruiterType;
        private Ability ability;
        @JsonProperty(value = "isLocationConsensusPossible")
        private boolean isLocationConsensusPossible;
        @JsonProperty(value = "isBookmarked")
        private boolean isBookmarked;
        @JsonProperty(value = "isExpired")
        private boolean isExpired;

        @Builder
        public ResponseRecentPost(String imgUrl, String nickname, String content, List<String> areas, int matchingCnt, LocalDate matchingDate, RecruiterType recruiterType, Ability ability, boolean isLocationConsensusPossible, boolean isBookmarked, boolean isExpired) {
            this.imgUrl = imgUrl;
            this.nickname = nickname;
            this.content = content;
            this.areas = areas;
            this.matchingCnt = matchingCnt;
            this.matchingDate = matchingDate;
            this.recruiterType = recruiterType;
            this.ability = ability;
            this.isLocationConsensusPossible = isLocationConsensusPossible;
            this.isBookmarked = isBookmarked;
            this.isExpired = isExpired;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseSearchPost {
        private String imgUrl;
        private String nickname;
        private String content;
        private List<String> areas;
        private int matchingCnt;
        private LocalDate matchingDate;
        private RecruiterType recruiterType;
        private Ability ability;
        @JsonProperty(value = "isLocationConsensusPossible")
        private boolean isLocationConsensusPossible;
        @JsonProperty(value = "isBookmarked")
        private boolean isBookmarked;
        @JsonProperty(value = "isExpired")
        private boolean isExpired;
        private String nextUrl;

        @Builder
        public ResponseSearchPost(String imgUrl, String nickname, String content, List<String> areas, int matchingCnt, LocalDate matchingDate, RecruiterType recruiterType, Ability ability, boolean isLocationConsensusPossible, boolean isBookmarked, boolean isExpired) {
            this.imgUrl = imgUrl;
            this.nickname = nickname;
            this.content = content;
            this.areas = areas;
            this.matchingCnt = matchingCnt;
            this.matchingDate = matchingDate;
            this.recruiterType = recruiterType;
            this.ability = ability;
            this.isLocationConsensusPossible = isLocationConsensusPossible;
            this.isBookmarked = isBookmarked;
            this.isExpired = isExpired;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RequestUpdatePost {
        @NotBlank
        private String content;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseMatchingPostDetail{
        private LocalDate teamCreateDate;
        private int teamMemberCnt;
        private String teamImgUrl;
        private String teamName;
        private double teamRating;
        private int reviewCnt;
        private LocalDate matchingDate;
        private List<String> areas;
        private Ability ability;
        private int participantsCnt;
        private List<String> participantsImgUrls;
        private RecruiterType recruiterType;
        private LocalDate expiryDate;
        private RecruitmentType recruitmentType;
        @JsonProperty(value = "isBookmarked")
        private boolean isBookmarked;
        @JsonProperty(value = "isWriter")
        private boolean isWriter;
        @JsonProperty(value = "isCompleted")
        private boolean isCompleted;

        @Builder
        public ResponseMatchingPostDetail(LocalDate teamCreateDate, int teamMemberCnt, String teamImgUrl, String teamName, double teamRating, int reviewCnt, LocalDate matchingDate, List<String> areas, Ability ability, int participantsCnt, List<String> participantsImgUrls, RecruiterType recruiterType, LocalDate expiryDate, RecruitmentType recruitmentType, boolean isBookmarked, boolean isWriter, boolean isCompleted) {
            this.teamCreateDate = teamCreateDate;
            this.teamMemberCnt = teamMemberCnt;
            this.teamImgUrl = teamImgUrl;
            this.teamName = teamName;
            this.teamRating = teamRating;
            this.reviewCnt = reviewCnt;
            this.matchingDate = matchingDate;
            this.areas = areas;
            this.ability = ability;
            this.participantsCnt = participantsCnt;
            this.participantsImgUrls = participantsImgUrls;
            this.recruiterType = recruiterType;
            this.expiryDate = expiryDate;
            this.recruitmentType = recruitmentType;
            this.isBookmarked = isBookmarked;
            this.isWriter = isWriter;
            this.isCompleted = isCompleted;
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class RequestSearchPost {
        @NotNull
        private SortOption sortOption;
        @NotNull @NotBlank
        private String query;
        private Long lastIdx;
        private Integer callCnt;
        private LocalDate lastExpiredDate;
    }
}
