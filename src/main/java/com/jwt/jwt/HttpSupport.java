package com.jwt.jwt;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class HttpSupport {

    public static Optional<Cookie> getCookie(HttpServletRequest req, String name) {
        return Stream.ofNullable(req.getCookies())
                .flatMap(Arrays::stream)
                .filter(cookie -> name.equals(cookie.getName()) && !cookie.getValue().isEmpty())
                .findFirst();
    }

    public static Cookie createCookie(UnaryOperator<CookieConfig> composer) {
        return composer.apply(new CookieConfig()).build();
    }

    public static void removeCookie(Cookie cookie, HttpServletResponse res) {
        System.out.println(cookie.getValue());

        Cookie removed = new CookieConfig()
                .name(cookie.getName()).value("").expires(10)
                .secure(cookie.getSecure())
                .build();
        res.addCookie(removed);
    }

    public static class CookieConfig {
        private String name;
        private String value;
        private int expires;
        private boolean secure;

        public CookieConfig name(String name) {
            this.name = name;
            return this;
        }

        public CookieConfig value(String value) {
            this.value = value;
            return this;
        }

        public CookieConfig expires(int expires) {
            this.expires = expires;
            return this;
        }

        public CookieConfig secure(boolean secure) {
            this.secure = secure;
            return this;
        }

        private Cookie build() {
            Cookie cookie = new Cookie(name, value);
            cookie.setMaxAge(expires);
            cookie.setSecure(secure);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            return cookie;
        }
    }
}
