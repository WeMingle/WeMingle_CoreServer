package com.wemingle.core.domain.post.dto;

import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.MatchingPostArea;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.locationselectiontype.LocationSelectionType;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import com.wemingle.core.global.annotation.Essential;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MatchingPostDto {
    @ToString
    @Setter
    @Getter
    public static class ResponseMatchingPostDto{
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
        public ResponseMatchingPostDto(String profilePicUrl, String writer, String contents, List<AreaName> areaList, int matchingCnt, LocalDate matchingDate, RecruiterType recruiterType, Ability ability, boolean isLocationConsensusPossible, boolean isBookmarked) {
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

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateMatchingPostDto {
        @NotNull
        private LocalDate matchingDate;
        @Essential
        private Double latitude;
        @Essential
        private Double longitude;
        @Essential
        private String locationName;
        @NotNull
        private List<AreaName> areaNameList;
        @NotNull
        private boolean isLocationConsensusPossible;
        @NotNull
        private Ability ability;
        private Gender gender;
        @NotNull
        @Min(value = 1, message = "capacityLimit must be greater than 1")
        private int capacityLimit;
        @NotNull
        private Long teamPk;
        private List<String> participantsId = new ArrayList<>(); // member pk
        @NotNull
        private LocalDate expiryDate;
        @NotNull
        private RecruiterType recruiterType;
        @NotNull
        private RecruitmentType recruitmentType;
        @NotNull
        private String content;
        @NotNull
        private LocationSelectionType locationSelectionType;

        @Builder
        public CreateMatchingPostDto(LocalDate matchingDate, Double latitude, Double longitude, String locationName, List<AreaName> areaNameList, boolean isLocationConsensusPossible, Ability ability, Gender gender, int capacityLimit, Long teamPk, List<String> participantsId, LocalDate expiryDate, RecruiterType recruiterType, RecruitmentType recruitmentType, String content, LocationSelectionType locationSelectionType) {
            this.matchingDate = matchingDate;
            this.latitude = latitude;
            this.longitude = longitude;
            this.locationName = locationName;
            this.areaNameList = areaNameList;
            this.isLocationConsensusPossible = isLocationConsensusPossible;
            this.ability = ability;
            this.gender = gender;
            this.capacityLimit = capacityLimit;
            this.teamPk = teamPk;
            this.participantsId = participantsId;
            this.expiryDate = expiryDate;
            this.recruiterType = recruiterType;
            this.recruitmentType = recruitmentType;
            this.content = content;
            this.locationSelectionType = locationSelectionType;
        }

        public MatchingPost of(Team team, TeamMember writer){
            GeometryFactory geometryFactory = new GeometryFactory();
            Coordinate coordinate = new Coordinate(longitude, latitude);
            Point position = geometryFactory.createPoint(coordinate);

            MatchingPost matchingPost = MatchingPost.builder()
                    .matchingDate(matchingDate)
                    .expiryDate(expiryDate)
                    .locationName(locationName)
                    .position(position)
                    .content(content)
                    .capacityLimit(capacityLimit)
                    .isLocationConsensusPossible(isLocationConsensusPossible)
                    .ability(ability)
                    .gender(gender)
                    .recruiterType(recruiterType)
                    .recruitmentType(recruitmentType)
                    .locationSelectionType(locationSelectionType)
                    .writer(writer)
                    .team(team)
                    .build();

            List<MatchingPostArea> areaList = areaNameList.stream().map(areaName -> new MatchingPostArea(areaName, matchingPost)).toList();
            matchingPost.putAreaList(areaList);

            return matchingPost;
        }
    }
}
