package com.wemingle.core.global.config.jwt;

import com.wemingle.core.domain.member.entity.role.Role;
import com.wemingle.core.global.config.jwt.exception.InvalidRoleException;
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

    //todo 학교 인증 전까지 role unVerifiedUser 처리
    public String generateRefreshToken(String memberEmail, Role role){
        return makeToken(new Date(new Date().getTime() + generateExpiredAt(REFRESH_TOKEN)), memberEmail, role);
    }

    public String generateAccessToken(String memberEmail, Role role){
        return makeToken(new Date(new Date().getTime() + generateExpiredAt(ACCESS_TOKEN)), memberEmail, role);
    }

    protected long generateExpiredAt(String tokenType){
        if (tokenType.equals(REFRESH_TOKEN)){
            return Period.ofMonths(6).toTotalMonths() * Duration.ofDays(30L).toMillis();
        }else {
            return Duration.ofDays(1).toMillis();
        }
    }

    public String makeToken(Date expiry, String memberEmail, Role role){
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject("WeMingle")
                .claim("email", memberEmail)
                .claim("role", ROLE_PREFIX + role.getRoleName())
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
            default -> throw new InvalidRoleException();
        }
    }

    private Authentication getAdminAuthentication(String jwtToken){
        String email = getMemberEmail(jwtToken);

        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(ROLE_PREFIX + "ADMIN"));
        User user = new User(email, "", authorities);

        return new UsernamePasswordAuthenticationToken(user, jwtToken, authorities);
    }

    private Authentication getUserAuthentication(String jwtToken){
        String email = getMemberEmail(jwtToken);

        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(ROLE_PREFIX + "USER"));
        User user = new User(email, "", authorities);

        return new UsernamePasswordAuthenticationToken(user, jwtToken, authorities);
    }

    public Claims getClaim(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    public String getMemberEmail(String jwtToken){
        Claims claims = getClaim(jwtToken);

        return String.valueOf(claims.get("email"));
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
}
