package com.jwt.jwt.security;

import com.jwt.jwt.support.HttpSupport;
import com.jwt.jwt.exceptions.AuthenticationException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.stream.Stream;

public class JwtInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;
    private final String COOKIE_KEY;

    public JwtInterceptor(JwtService jwts, String cookie) {
        this.jwtService = jwts;
        this.COOKIE_KEY = cookie;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        // 인증 target 이 아닌경우 pass
        if (!(handler instanceof HandlerMethod) || !isAuthenticationPresent((HandlerMethod) handler)) {
            return true;
        }

        Optional<String> header = getAuthorizationToken(request);
        Optional<String> cookie = HttpSupport.getCookie(request, COOKIE_KEY).map(Cookie::getValue);

        return Stream.concat(header.stream(), cookie.stream())
                .map(jwtService::isUsable)
                .filter(check -> check)
                .findFirst()
                .orElseThrow(() -> new AuthenticationException("Unauthorized access. need to authentication"));
    }

    private boolean isAuthenticationPresent(HandlerMethod handler) {
        return handler.hasMethodAnnotation(Authenticated.class)
                || handler.getBeanType().isAnnotationPresent(Authenticated.class);
    }

    private Optional<String> getAuthorizationToken(HttpServletRequest req) {
        return Optional.ofNullable(req.getHeader("Authorization"))
                .map(token -> token.replaceAll("Bearer", "").trim());
    }
}
