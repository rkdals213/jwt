package com.jwt.jwt.exceptions;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Message {
    private final Throwable throwable;
    private final String message;

    public static MessageBuilder builder() {
        return new MessageBuilder() {
            @Override
            public Message build() {
                return super.build();
            }
        };
    }

    public static class MessageBuilder {
        public MessageBuilder message(String message) {
            this.message = message;
            return this;
        }

        public MessageBuilder message(String format, Object... params) {
            this.message = String.format(format, params);
            return this;
        }
    }
}
