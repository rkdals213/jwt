package com.jwt.jwt.security;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface JwtService {
    String create(Consumer<Payload> composer);
    String create(Payload composer);
    boolean isUsable(String token);
    String parseClaim(String token);

    @Getter
    class Payload {
        @Setter private ZonedDateTime exp;
        private final Map<String, Object> claims = new HashMap<>();

        public void addClaim(String key, Object value) {
            claims.put(key, value);
        }
    }
}