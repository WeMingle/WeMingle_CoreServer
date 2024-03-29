package com.wemingle.core.domain.member.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.member.entity.phonetype.PhoneType;
import com.wemingle.core.domain.member.entity.role.Role;
import com.wemingle.core.domain.member.entity.signupplatform.SignupPlatform;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @NotNull
    @Column(name = "MEMBER_ID", columnDefinition = "VARBINARY(255) NOT NULL")
    private String memberId;

    @NotNull
    @Column(name = "PASSWORD", columnDefinition = "VARBINARY(255) NOT NULL")
    private String password;

    @NotNull
    @Column(name = "NICKNAME", columnDefinition = "VARBINARY(255) NOT NULL")
    private String nickname;

    @NotNull
    @Column(name = "PROFILE_IMG_ID", columnDefinition = "VARBINARY(255) NOT NULL")
    private UUID profileImgId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "PHONE_TYPE")
    private PhoneType phoneType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "SIGNUP_PLATFORM")
    private SignupPlatform signupPlatform;

    @NotNull
    @Column(name = "REFRESH_TOKEN", columnDefinition = "VARBINARY(400) NOT NULL")
    private String refreshToken;

    @NotNull
    @Column(name = "FIREBASE_TOKEN", columnDefinition = "VARBINARY(400) NOT NULL")
    private String firebaseToken;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE")
    private Role role;

    @NotNull
    @Column(name = "COMPLAINTS_COUNT")
    private int complaintsCount;

    @NotNull
    @Column(name = "NOTIFY_ALLOW")
    private boolean notifyAllow;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POLICY_TERMS")
    private PolicyTerms policyTerms;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.getRoleName()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return memberId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Builder
    public Member(String memberId, String password, String nickname, UUID profileImgId, PhoneType phoneType, SignupPlatform signupPlatform, String refreshToken, String firebaseToken, Role role, boolean notifyAllow, PolicyTerms policyTerms) {
        this.memberId = memberId;
        this.password = password;
        this.nickname = nickname;
        this.profileImgId = profileImgId;
        this.phoneType = phoneType;
        this.signupPlatform = signupPlatform;
        this.refreshToken = refreshToken;
        this.firebaseToken = firebaseToken;
        this.role = role;
        this.complaintsCount = 0;
        this.notifyAllow = notifyAllow;
        this.policyTerms = policyTerms;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }


    public void setMemberProfile(UUID profileImgId, String nickname) {
        this.profileImgId = profileImgId;
        this.nickname = nickname;
    }

    public void patchRefreshToken(String newRefreshToken){
        this.refreshToken = newRefreshToken;
    }
    public void patchMemberProfile(String nickname) {
        this.nickname = nickname;
    }

    public void convertToAuthenticationUser(String newRefreshToken){
        this.refreshToken = newRefreshToken;
        this.role = Role.USER;
    }

    public void patchPolicyTerms(PolicyTerms policyTerms){
        this.policyTerms = policyTerms;
    }
}
