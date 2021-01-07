package com.jwt.jwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class ConsoleJwtService implements JwtService {
    private final static byte[] JWT_KEY_SALT = "dlsdjrywngowjreks".getBytes(StandardCharsets.UTF_8);
    private final static byte[] JWT_KEY = "dndbsmschzhdndbrkchlrhdi".getBytes(StandardCharsets.UTF_8);

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final Logger log = LoggerFactory.getLogger(ConsoleJwtService.class);

    private Key getSecretKey() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(JWT_KEY);
            md.update(JWT_KEY_SALT);
            return Keys.hmacShaKeyFor(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String create(Consumer<Payload> composer) {
        Payload payload = new Payload();
        composer.accept(payload);
        return create(payload);
    }

    public String create(Payload payload) {
        JwtBuilder builder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setExpiration(Date.from(payload.getExp().toInstant()))
                .signWith(getSecretKey());

        for (Map.Entry<String, Object> entry : payload.getClaims().entrySet()) {
            builder.claim(entry.getKey(), entry.getValue());
        }

        return builder.compact();
    }

    @Override
    public boolean isUsable(String token) {
        return checkJwt(token);
    }

    @Override
    public String parseClaim(String token) {
        return parseJwt(token);
    }

    private boolean checkJwt(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
            Date expiration = claims.getBody().getExpiration();

            return System.currentTimeMillis() <= expiration.getTime();
        } catch (Exception e) {
            log.info("Fail to check web token");
            log.debug("Fail to check web token", e);
            return false;
        }
    }

    private String parseJwt(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
            return jsonMapper.writeValueAsString(claims.getBody());
        } catch (Exception e) {
            log.info("Fail to parse web token");
            log.debug("Fail to parse web token", e);
            return null;
        }
    }
}
