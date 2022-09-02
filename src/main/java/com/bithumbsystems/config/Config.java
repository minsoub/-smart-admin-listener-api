package com.bithumbsystems.config;

import lombok.Getter;

@Getter
public class Config {
    private final String baseMessage;

    public Config(String baseMessage) {
        this.baseMessage = baseMessage;
    }
}
