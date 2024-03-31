package com.wemingle.core.global.config.jwt;

import com.wemingle.core.domain.member.entity.role.Role;
import com.wemingle.core.global.exception.InvalidRoleException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Period;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {
    private final JwtProperties jwtProperties;

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String REFRESH_TOKEN= "refreshToken";
    private static final String ACCESS_TOKEN = "accessToken";

    public String createRefreshToken(String memberId, Role role){
        return createToken(new Date(new Date().getTime() + createExpiredAt(REFRESH_TOKEN)), memberId, role);
    }

    public String createAccessToken(String memberId, Role role){
        return createToken(new Date(new Date().getTime() + createExpiredAt(ACCESS_TOKEN)), memberId, role);
    }

    private long createExpiredAt(String tokenType){
        if (tokenType.equals(REFRESH_TOKEN)){
            return Period.ofMonths(6).toTotalMonths() * Duration.ofDays(30L).toMillis();
        }else {
            return Duration.ofDays(1).toMillis();
        }
    }

    public String createToken(Date expiry, String memberId, Role role){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject("WeMingle")
                .claim("id", memberId)
                .claim("role", role.getRoleName())
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validToken(String jwtToken){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(jwtToken);

            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean isExpired(String jwtToken){
        Object detailedValidate = detailedValidateToken(jwtToken);

        return detailedValidate instanceof ExpiredJwtException;
    }

    private Object detailedValidateToken(String jwtToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(jwtToken);
            return true;
        } catch (JwtException e) {
            return e;
        }
    }

    public Authentication getAuthentication(String jwtToken){
        String role = getRole(jwtToken);

        switch (role){
            case "ROLE_ADMIN" -> {
                return getAdminAuthentication(jwtToken);
            }
            case "ROLE_USER" -> {
                return getUserAuthentication(jwtToken);
            }
            case "ROLE_UNVERIFIED_USER" -> {
                return getUnVerifiedUserAuthentication(jwtToken);
            }
            default -> throw new InvalidRoleException();
        }
    }

    private Authentication getAdminAuthentication(String jwtToken){
        String id = getMemberId(jwtToken);

        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(Role.ADMIN.getRoleName()));
        User user = new User(id, "", authorities);

        return new UsernamePasswordAuthenticationToken(user, jwtToken, authorities);
    }

    private Authentication getUserAuthentication(String jwtToken){
        String id = getMemberId(jwtToken);

        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(Role.USER.getRoleName()));
        User user = new User(id, "", authorities);

        return new UsernamePasswordAuthenticationToken(user, jwtToken, authorities);
    }

    private Authentication getUnVerifiedUserAuthentication(String jwtToken){
        String id = getMemberId(jwtToken);

        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(Role.UNVERIFIED_USER.getRoleName()));
        User user = new User(id, "", authorities);

        return new UsernamePasswordAuthenticationToken(user, jwtToken, authorities);
    }

    public Claims getClaim(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    public String getMemberId(String jwtToken){
        Claims claims = getClaim(jwtToken);

        return String.valueOf(claims.get("id"));
    }

    public String getRole(String jwtToken){
        Claims claims = getClaim(jwtToken);

        return String.valueOf(claims.get("role"));
    }

    public Duration getRemainingTokenExpirationTime(String jwtToken){
        Claims claim = getClaim(jwtToken);
        Date expirationDate = claim.getExpiration();

        return Duration.ofMillis(expirationDate.getTime() - System.currentTimeMillis());
    }

    public Date getExpirationTime(String jwtToken){
        Claims claim = getClaim(jwtToken);
        return claim.getExpiration();
    }
}
