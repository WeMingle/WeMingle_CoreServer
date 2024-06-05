package com.wemingle.core.global.initdb;


import com.wemingle.core.domain.bookmark.entity.BookmarkedMatchingPost;
import com.wemingle.core.domain.bookmark.repository.BookmarkMatchingPostRepository;
import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.PolicyTerms;
import com.wemingle.core.domain.member.entity.phonetype.PhoneType;
import com.wemingle.core.domain.member.entity.role.Role;
import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.member.repository.PolicyTermsRepository;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.MatchingPostArea;
import com.wemingle.core.domain.post.entity.MatchingPostMatchingDate;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Profile("jungwoo")
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
    BookmarkMatchingPostRepository bookmarkRepository;

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
        ArrayList<MatchingPost> matchingPost = createMatchingPost(10000, teamMemberRepositoryAll, teamList);
        List<MatchingPostArea> matchingPostArea = createMatchingPostArea(matchingPost);
        List<MatchingPostMatchingDate> matchingDate = createMatchingPostMatchingDate(matchingPost);
        for (int i = 0; i < 10000; i++) {
            matchingPost.get(i).putArea(matchingPostArea.get(i));
            matchingPost.get(i).putMatchingDates(List.of(matchingDate.get(i)));
        }
        List<MatchingPost> matchingPosts = matchingPostRepository.saveAll(matchingPost);

        bookmarkRepository.saveAll(createBookmark(10000, matchingPosts, memberRepositoryAll));
    }

    private List<BookmarkedMatchingPost> createBookmark(int amount, List<MatchingPost> matchingPosts, List<Member> members) {
        List<BookmarkedMatchingPost> returnBookmark = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            BookmarkedMatchingPost bookmarkedMatchingPost = BookmarkedMatchingPost.builder()
                    .matchingPost(matchingPosts.get(i))
                    .member(members.get(i%10))
                    .build();

            returnBookmark.add(bookmarkedMatchingPost);
        }

        return returnBookmark;
    }

    private List<MatchingPostArea> createMatchingPostArea(ArrayList<MatchingPost> matchingPosts) {
        return matchingPosts.stream().map(m -> MatchingPostArea.builder().areaName(AreaName.경기).matchingPost(m).build()).toList();
    }

    private List<MatchingPostMatchingDate> createMatchingPostMatchingDate(ArrayList<MatchingPost> matchingPosts) {
        return matchingPosts.stream().map(m -> MatchingPostMatchingDate.builder().matchingDate(LocalDate.of(2024, new Random().nextInt(1,12), new Random().nextInt(1,30))).matchingPost(m).build()).toList();
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


    private List<Team> createTeam(int amount,List<Member> memberList) {
        List<Team> returnTeams = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Team team = Team.builder()
                    .teamName("teamname" + i)
                    .capacityLimit(100)
                    .profileImgId(UUID.randomUUID())
                    .content("teamname" + i)
                    .recruitmentType(RecruitmentType.FIRST_SERVED_BASED)
                    .teamOwner(memberList.get(i))
                    .teamType(TeamType.TEAM)
                    .sportsCategory(SportsType.OTHER)
                    .build();

            returnTeams.add(team);
        }

        return returnTeams;
    }

    private ArrayList<MatchingPost> createMatchingPost(int amount, List<TeamMember> memberList, List<Team> teams) {
        ArrayList<MatchingPost> matchingPosts = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            matchingPosts.add(MatchingPost.builder()
                    .expiryDate(LocalDate.of(2024, new Random().nextInt(12)+1, new Random().nextInt(29)+1))
                    .locationName("jacob house")
                    .lat(new Random().nextDouble(90))
                    .lon(new Random().nextDouble(180))
                    .content("msg")
                    .capacityLimit(10)
                    .isLocationConsensusPossible(true)
                    .ability(Ability.MEDIUM)
                    .gender(Gender.MALE)
                    .recruiterType(RecruiterType.INDIVIDUAL)
                    .recruitmentType(RecruitmentType.APPROVAL_BASED)
                    .locationSelectionType(LocationSelectionType.SELECT_BASED)
                    .writer(memberList.get(amount%10))
                    .team(teams.get(amount%10))
                    .viewCnt(new Random().nextInt(100))//new Random().nextInt(100000)
                    .sportsCategory(SportsType.OTHER)
                    .build());
        }
        return matchingPosts;
    }

    private void createMember(int amount) {
        for (int i = 0; i < amount-1; i++) {
            memberRepository.save(Member.builder()
                    .memberId("memberId" + i)
                    .gender(Gender.MALE)
                    .majorActivityArea(AreaName.강원)
                    .oneLineIntroduction("안녕")
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
        memberRepository.save(Member.builder()
                .memberId("wemingle@gmail.com")
                .gender(Gender.MALE)
                .majorActivityArea(AreaName.강원)
                .oneLineIntroduction("안녕")
                .password("password")
                .nickname("leeking")
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