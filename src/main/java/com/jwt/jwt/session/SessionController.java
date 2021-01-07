package com.jwt.jwt.session;

import com.jwt.jwt.HttpSupport;
import com.jwt.jwt.security.Authenticated;
import com.jwt.jwt.security.ConsoleJwtService;
import com.jwt.jwt.security.JwtClaim;
import com.jwt.jwt.security.OAuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SessionController {
    public final static String JWT_COOKIE_NAME = "my.test.jwt";

    private final OAuthClient oauthClient;
    private final ConsoleJwtService jwtService;

    @PostMapping("/session")
    public Map<String, Object> createSession(@RequestBody Map<String, String> input, HttpServletRequest req, HttpServletResponse res) {
        Map<String, Object> profile = Map.of(
                "id", input.get("id"),
                "pw", input.get("pw")
        );
        OAuthClient.Token token = oauthClient.exchangeToken();
        String jwt = jwtService.create(
                payload -> {
                    payload.setExp(token.getExpiration());
                    payload.addClaim("info", profile);
                    System.out.println(payload.getClaims());
                });

        Cookie jwtCookie = HttpSupport.createCookie(conf -> conf
                .name(JWT_COOKIE_NAME)
                .value(jwt)
                .expires(60 * 60 * 24)
                .secure("https".equals(req.getScheme()))
        );

        res.addCookie(jwtCookie);

        return Map.of(
                "token", jwt,
                "expiration", token.getExpiration().toString(),
                "crew", profile
        );
    }

    @GetMapping("/session")
    public Map<String, Object> getSession(HttpServletRequest req, HttpServletResponse res) {

        return Map.of(
                "token", HttpSupport.getCookie(req, JWT_COOKIE_NAME).map(Cookie::getValue),
                "tokenName", HttpSupport.getCookie(req, JWT_COOKIE_NAME).map(Cookie::getName)
        );
    }

    @DeleteMapping("/session")
    public Map<String, Object> revokeSession(HttpServletRequest req, HttpServletResponse res) {
        HttpSupport.getCookie(req, JWT_COOKIE_NAME)
                .ifPresent(cookie -> HttpSupport.removeCookie(cookie, res));
        return Map.of(
                "code", "session.revoked",
                "message", "t.pirates session has been revoked."
        );
    }

    @Authenticated
    @GetMapping("/testApi")
    public Map<String, Object> testApi(@JwtClaim("info.id") String id, @JwtClaim("info.pw") String pw) {

        return Map.of(
                "id", id,
                "pw", pw
        );
    }

}
