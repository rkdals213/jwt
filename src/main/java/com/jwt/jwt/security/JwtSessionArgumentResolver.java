package com.jwt.jwt.security;

import com.jayway.jsonpath.JsonPath;
import com.jwt.jwt.support.HttpSupport;
import com.jwt.jwt.exceptions.AuthenticationException;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class JwtSessionArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtService jwtService;
    private final String COOKIE_KEY;

    public JwtSessionArgumentResolver(JwtService jwtService, String cookie) {
        this.jwtService = jwtService;
        this.COOKIE_KEY = cookie;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(JwtClaim.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        JwtClaim annotation = parameter.getParameterAnnotation(JwtClaim.class);
        Class<?> paramType = parameter.getParameterType();
        String path = String.format("$.%s", annotation.value());
        System.out.println(path);

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        Optional<String> header = getAuthorizationToken(request);
        Optional<String> cookie = HttpSupport.getCookie(request, COOKIE_KEY).map(Cookie::getValue);

        return Stream.concat(header.stream(), cookie.stream())
                .map(jwtService::parseClaim)
                .filter(Objects::nonNull)
                .findFirst()
                .map(claim -> JsonPath.parse(claim).read(path, paramType))
                .orElseThrow(() -> new AuthenticationException("Unavailable web token!!!"));
    }

    private Optional<String> getAuthorizationToken(HttpServletRequest req) {
        return Optional.ofNullable(req.getHeader("Authorization"))
                .map(token -> token.replaceAll("Bearer", "").trim());
    }
}
