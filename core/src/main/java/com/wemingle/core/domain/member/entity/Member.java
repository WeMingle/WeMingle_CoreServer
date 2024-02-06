package com.wemingle.core.domain.member.entity;

import com.wemingle.core.domain.member.entity.role.Role;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MEMBER_ID")
    private UUID memberId;

    @NotNull
    @Column(name = "NICKNAME", columnDefinition = "VARBINARY(255) NOT NULL")
    private String nickname;

    @NotNull
    @Column(name = "MEMBER_NAME", columnDefinition = "VARBINARY(255) NOT NULL")
    private String memberName;

    @NotNull
    @Column(name = "PHONE_NUMBER", columnDefinition = "VARBINARY(255) NOT NULL")
    private String phoneNumber;

    @NotNull
    @Column(name = "DATE_OF_BIRTH", columnDefinition = "VARBINARY(255) NOT NULL")
    private LocalDate dateOfBirth;

    @NotNull
    @Column(name = "EMAIL", columnDefinition = "VARBINARY(255) NOT NULL")
    private String email;

    @NotNull
    @Column(name = "PASSWORD", columnDefinition = "VARBINARY(255) NOT NULL")
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE")
    private Role role;

    @Column(name = "REFRESH_TOKEN", columnDefinition = "VARBINARY (400) NOT NULL")
    private String refreshToken;

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
        return memberName;
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
    public Member(UUID memberId, String nickname, String memberName, String phoneNumber, LocalDate dateOfBirth, String email, String password, Role role) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.memberName = memberName;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public void patchRefreshToken(String newRefreshToken){
        this.refreshToken = newRefreshToken;
    }
}
