package com.jwt.jwt.security;

public @interface Authorized {
    String[] authorities();
}
