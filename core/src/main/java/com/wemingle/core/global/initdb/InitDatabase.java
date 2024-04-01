package com.wemingle.core.global.initdb;


import com.wemingle.core.domain.category.sports.entity.SportsCategory;
import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.category.sports.repository.SportsCategoryRepository;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.PolicyTerms;
import com.wemingle.core.domain.member.entity.phonetype.PhoneType;
import com.wemingle.core.domain.member.entity.role.Role;
import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.member.repository.PolicyTermsRepository;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.MatchingPostArea;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.locationselectiontype.LocationSelectionType;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.post.repository.MatchingPostAreaRepository;
import com.wemingle.core.domain.post.repository.MatchingPostRepository;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import com.wemingle.core.domain.team.entity.teamrole.TeamRole;
import com.wemingle.core.domain.team.entity.teamtype.TeamType;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import com.wemingle.core.domain.team.repository.TeamRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Profile("prod")
@Component
public class InitDatabase {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PolicyTermsRepository policyTermsRepository;
    @Autowired
    MatchingPostRepository matchingPostRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    TeamMemberRepository teamMemberRepository;
    @Autowired
    MatchingPostAreaRepository matchingPostAreaRepository;
    @Autowired
    SportsCategoryRepository sportsCategoryRepository;

    @PostConstruct
    public void InitDatabase() throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.info("initinit");

        createMember(10);
        List<Member> memberRepositoryAll = memberRepository.findAll();
        List<Team> teams = createTeam(10, memberRepositoryAll);
        List<TeamMember> teamMembers = createTeamMember(10, memberRepositoryAll, teams);
        for (int i = 0; i < 10; i++) {
            teams.get(i).addTeamMember(teamMembers.get(i));
        }
        List<Team> teamList = teamRepository.saveAll(teams);
        List<TeamMember> teamMemberRepositoryAll = teamMemberRepository.findAll();
        ArrayList<MatchingPost> matchingPost = createMatchingPost(10, teamMemberRepositoryAll, teamList);
        List<MatchingPostArea> matchingPostArea = createMatchingPostArea(matchingPost);
        for (int i = 0; i < 10; i++) {
            matchingPost.get(i).putArea(matchingPostArea.get(i));
        }
        matchingPostRepository.saveAll(matchingPost);
    }

    private List<MatchingPostArea> createMatchingPostArea(ArrayList<MatchingPost> matchingPosts) {
        return matchingPosts.stream().map(m -> MatchingPostArea.builder().areaName(AreaName.경기).matchingPost(m).build()).toList();
    }

    private List<TeamMember> createTeamMember(int amount, List<Member> memberList, List<Team> teams) {
        List<TeamMember> returnTeamMembers = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            TeamMember teamMember = TeamMember.builder()
                    .nickname("nickname" + i)
                    .profileImg(UUID.randomUUID())
                    .teamRole(TeamRole.PARTICIPANT)
                    .member(memberList.get(i))
                    .team(teams.get(i))
                    .build();

            returnTeamMembers.add(teamMember);
        }

        return returnTeamMembers;
    }

    private SportsCategory createSportsCategory() {
        return sportsCategoryRepository.save(SportsCategory.builder().sportsName(SportsType.OTHER).build());
    }

    private List<Team> createTeam(int amount,List<Member> memberList) {
        List<Team> returnTeams = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Team team = Team.builder()
                    .teamName("teamname" + i)
                    .capacityLimit(100)
                    .profileImgId(UUID.randomUUID())
                    .teamOwner(memberList.get(i))
                    .teamType(TeamType.TEAM)
                    .sportsCategory(createSportsCategory())
                    .build();

            returnTeams.add(team);
        }

        return returnTeams;
    }

    private ArrayList<MatchingPost> createMatchingPost(int amount, List<TeamMember> memberList, List<Team> teams) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(12.123, 123.123);
        Point point = geometryFactory.createPoint(coordinate);
        ArrayList<MatchingPost> matchingPosts = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            matchingPosts.add(MatchingPost.builder()
                    .matchingDate(LocalDate.of(2024, 3, 29))
                    .expiryDate(LocalDate.of(2024, 3, 29))
                    .locationName("jacob house")
                    .position(point)
                    .content("msg")
                    .capacityLimit(10)
                    .isLocationConsensusPossible(true)
                    .ability(Ability.MEDIUM)
                    .gender(Gender.MALE)
                    .recruiterType(RecruiterType.INDIVIDUAL)
                    .recruitmentType(RecruitmentType.APPROVAL_BASED)
                    .locationSelectionType(LocationSelectionType.SELECT_BASED)
                    .writer(memberList.get(i))
                    .team(teams.get(i))
                    .build());
        }
        return matchingPosts;
    }

    private void createMember(int amount) {
        for (int i = 0; i < amount; i++) {
            memberRepository.save(Member.builder().memberId("memberId" + i)
                    .password("password")
                    .nickname("nickname" + i)
                    .profileImgId(UUID.randomUUID())
                    .phoneType(PhoneType.AOS)
                    .signupPlatform(SignupPlatform.APPLE)
                    .refreshToken("token")
                    .firebaseToken("fire")
                    .role(Role.USER)
                    .notifyAllow(true)
                    .policyTerms(policyTermsRepository.save(new PolicyTerms(true, true)))
                    .build());
        }
    }


}