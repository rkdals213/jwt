package com.jwt.jwt.exceptions;

import java.util.function.UnaryOperator;

public class AuthenticationException extends AbstractException{
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(UnaryOperator<Message.MessageBuilder> composer) {
        super(composer.apply(Message.builder()).build());
    }
}