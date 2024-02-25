package com.wemingle.core.domain.user.entity;

import com.wemingle.core.domain.user.entity.phonetype.PhoneType;
import com.wemingle.core.domain.user.entity.role.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_PK")
    private Long memberPk;

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
    private String profileImgId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "PHONE_TYPE")
    private PhoneType phoneType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "SIGNUP_PLATFORM")
    private PhoneType signupPlatform;

    @NotNull
    @Column(name = "SIGNUP_DATE")
    private LocalDate signupDate;

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
    public Member(Long memberPk, String memberId, String password, String nickname, String profileImgId, PhoneType phoneType, PhoneType signupPlatform, LocalDate signupDate, String refreshToken, String firebaseToken, Role role, int complaintsCount) {
        this.memberPk = memberPk;
        this.memberId = memberId;
        this.password = password;
        this.nickname = nickname;
        this.profileImgId = profileImgId;
        this.phoneType = phoneType;
        this.signupPlatform = signupPlatform;
        this.signupDate = signupDate;
        this.refreshToken = refreshToken;
        this.firebaseToken = firebaseToken;
        this.role = role;
        this.complaintsCount = complaintsCount;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
