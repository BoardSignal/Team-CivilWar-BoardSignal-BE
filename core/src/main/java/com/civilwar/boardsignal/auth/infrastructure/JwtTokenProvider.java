package com.civilwar.boardsignal.auth.infrastructure;

import com.civilwar.boardsignal.auth.domain.RefreshTokenRepository;
import com.civilwar.boardsignal.auth.domain.TokenProvider;
import com.civilwar.boardsignal.auth.domain.model.Token;
import com.civilwar.boardsignal.auth.domain.model.TokenPayload;
import com.civilwar.boardsignal.auth.exception.AuthErrorCode;
import com.civilwar.boardsignal.auth.properties.JwtProperty;
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
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenProvider {

    private static final String JWT_ROLE = "JWT_ROLE";
    private final JwtProperty jwtProperty;
    private final RefreshTokenRepository refreshTokenRepository;

    private String generateToken(
        Long id,
        Role role,
        long expireTime
    ) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime);
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperty.getClientSecret());

        Claims claims = Jwts.claims().setSubject(String.valueOf(id));
        claims.put(JWT_ROLE, role);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expireDate)
            .signWith(Keys.hmacShaKeyFor(keyBytes), SignatureAlgorithm.HS256)
            .compact();
    }

    private Jws<Claims> getClaims(String token) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperty.getClientSecret());

        try {
            return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(keyBytes))
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

        long accessExpiryMinute = jwtProperty.getAccessExpiryTime() * 1000 * 60;
        long refreshExpiryMinute = jwtProperty.getRefreshExpiryTime() * 1000 * 60;

        String accessToken = generateToken(id, role, accessExpiryMinute);
        String refreshToken = generateToken(id, role, refreshExpiryMinute);

        //로그인 할 때마다 RefreshToken 갱신
        String uuid = UUID.randomUUID().toString();
        refreshTokenRepository.save(uuid, refreshToken, jwtProperty.getRefreshExpiryTime());

        return new Token(accessToken, uuid, role);
    }

    @Override
    public String issueAccessToken(String refreshTokenId) {

        //refreshToken 조회
        String refreshToken = refreshTokenRepository.findById(refreshTokenId)
            .orElseThrow(() -> new ValidationException(AuthErrorCode.AUTH_TOKEN_EXPIRED));

        //refreshToken을 통해 유저 정보 조회
        TokenPayload payLoad = getPayLoad(refreshToken);

        //accessToken 재발급
        long accessExpiryMinute = jwtProperty.getAccessExpiryTime() * 1000 * 60;
        return generateToken(payLoad.userId(), payLoad.role(), accessExpiryMinute);
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
