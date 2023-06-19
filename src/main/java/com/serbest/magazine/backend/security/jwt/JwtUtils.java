package com.serbest.magazine.backend.security.jwt;

import com.serbest.magazine.backend.entity.Author;
import com.serbest.magazine.backend.exception.CustomApplicationException;
import com.serbest.magazine.backend.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import org.springframework.web.util.WebUtils;

import java.security.Key;
import java.util.Date;


@Component
public class JwtUtils {

    @Value("${magazine.app.accessTokenSecret}")
    private String accessTokenSecret;

    @Value("${magazine.app.activationTokenSecret}")
    private String activationTokenSecret;

    @Value("${magazine.app.accessTokenExpirationMs}")
    private int accessTokenExpirationMs;

    @Value("${magazine.app.activateTokenExpirationMs}")
    private int activateTokenExpirationMs;

    @Value("${magazine.app.jwtRefreshCookieName}")
    private String jwtRefreshCookie;

    // Access Token
    public String generateAccessToken(UserDetailsImpl userDetails) {
        return generateAccessTokenFromJWT(userDetails.getUsername());
    }

    public String generateAccessToken(Author author) {
        return generateAccessTokenFromJWT(author.getUsername());
    }

    public String generateValidationToken(Author author) {
        return generateValidationTokenFromJWTWithUserEmail(author.getEmail());
    }

    public ResponseCookie generateRefreshJwtCookie(String refreshToken) {
        return generateCookie(jwtRefreshCookie, refreshToken, "/api/auth/refreshToken");
    }

    public String getJwtRefreshFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtRefreshCookie);
    }

    public ResponseCookie getCleanJwtRefreshCookie() {
        ResponseCookie cookie = ResponseCookie
                .from(jwtRefreshCookie, null)
                .build();
        return cookie;
    }


    private Key generateAccessTokenKey() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(accessTokenSecret)
        );
    }

    private Key generateActivationTokenKey() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(activationTokenSecret)
        );
    }

    public String getUserNameOrEmailFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(generateAccessTokenKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        String usernameOrEmail = claims.getSubject();
        return usernameOrEmail;
    }

    public String getUserEmailFromJwtActivationToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(generateActivationTokenKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        String usernameOrEmail = claims.getSubject();
        return usernameOrEmail;
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(generateAccessTokenKey())
                    .build()
                    .parse(accessToken);
            return true;
        } catch (MalformedJwtException ex) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Token geçersiz durumda!");
        } catch (ExpiredJwtException ex) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Token süresi doldu!");
        } catch (UnsupportedJwtException ex) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Desteklenmeyen Token!");
        } catch (IllegalArgumentException ex) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Eyvah ! Token oluşumunda bir illegal durumu tespit edldi!");
        }
    }

    public boolean validateActivationToken(String activationToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(generateActivationTokenKey())
                    .build()
                    .parse(activationToken);
            return true;
        } catch (MalformedJwtException ex) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Token geçersiz durumda!");
        } catch (ExpiredJwtException ex) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Token süresi doldu!");
        } catch (UnsupportedJwtException ex) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Desteklenmeyen Token!");
        } catch (IllegalArgumentException ex) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Eyvah ! Token oluşumunda bir illegal durumu tespit edldi!");
        }
    }

    public String generateAccessTokenFromJWT(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + accessTokenExpirationMs))
                .signWith(generateAccessTokenKey())
                .compact();
    }

    public String generateValidationTokenFromJWTWithUserEmail(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + activateTokenExpirationMs))
                .signWith(generateActivationTokenKey())
                .compact();
    }

    private ResponseCookie generateCookie(String name, String value, String path) {
        ResponseCookie cookie = ResponseCookie.from(name, value).path(path).sameSite("None").maxAge(24 * 60 * 60).secure(true).httpOnly(true).build();
        return cookie;
    }

    private String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

}
