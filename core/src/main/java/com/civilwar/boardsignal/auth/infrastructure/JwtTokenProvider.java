package com.civilwar.boardsignal.auth.infrastructure;

import com.civilwar.boardsignal.auth.domain.TokenProvider;
import com.civilwar.boardsignal.auth.domain.model.Token;
import com.civilwar.boardsignal.auth.domain.model.TokenPayload;
import com.civilwar.boardsignal.auth.exception.AuthErrorCode;
import com.civilwar.boardsignal.common.exception.ValidationException;
import com.civilwar.boardsignal.user.domain.constants.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider implements TokenProvider {

    private static final String JWT_ROLE = "JWT_ROLE";
    private final long accessExpiryTime;
    private final long refreshExpiryTime;
    private final Key key;


    public JwtTokenProvider(
        @Value("${jwt.client-secret}") String clientSecret,
        @Value("${jwt.access-expiry-time}") int accessExpiryTime,
        @Value("${jwt.refresh-expiry-time}") int refreshExpiryTime
    ) {
        this.accessExpiryTime = accessExpiryTime;
        this.refreshExpiryTime = refreshExpiryTime;

        byte[] keyBytes = Decoders.BASE64.decode(clientSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateToken(
        Long id,
        Role role,
        long expireTime
    ) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime);

        Claims claims = Jwts.claims().setSubject(String.valueOf(id));
        claims.put(JWT_ROLE, role);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expireDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    private Jws<Claims> getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new ValidationException(AuthErrorCode.AUTH_TOKEN_EXPIRED);
        } catch (
            SecurityException |
            MalformedJwtException |
            UnsupportedJwtException |
            IllegalArgumentException e
        ) {
            throw new ValidationException(AuthErrorCode.AUTH_TOKEN_INVALID);
        }
    }

    @Override
    public Token createToken(Long id, Role role) {
        String accessToken = generateToken(id, role, accessExpiryTime);
        String refreshToken = generateToken(id, role, refreshExpiryTime);

        return new Token(accessToken, refreshToken, role);
    }

    @Override
    public TokenPayload getPayLoad(String token) {
        Jws<Claims> claims = getClaims(token);
        Long userId = Long.parseLong(claims.getBody().getSubject());
        String name = claims.getBody().get(JWT_ROLE).toString();
        Role role = Role.valueOf(name);

        return new TokenPayload(userId, role);
    }

    @Override
    public void validateToken(String token) {
        getClaims(token);
    }
}
