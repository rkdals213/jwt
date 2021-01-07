package com.jwt.jwt.security;

import com.jwt.jwt.exceptions.AuthenticationException;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
public class OAuthClient {
    @Getter
    @ToString
    public static class Token {
        private ZonedDateTime expiration;
    }

    public Token exchangeToken() {
        try {
            Token token = new Token();
            long now = Instant.now().getEpochSecond();

            token.expiration = Instant.ofEpochSecond(now + 60*60*24).atZone(ZoneOffset.UTC);

            return token;
        } catch (JSONException e) {
            throw new AuthenticationException("Fail to parse token!!");
        }
    }
}
