package com.jwt.jwt.exceptions;

import java.util.function.UnaryOperator;

public abstract class AbstractException extends RuntimeException {

    public AbstractException(String message) {
        super(message);
    }

    public AbstractException(UnaryOperator<Message.MessageBuilder> composer) {
        this(composer.apply(Message.builder()).build());
    }

    protected AbstractException(Message message) {
        super(message.getMessage(), message.getThrowable());
    }
}
