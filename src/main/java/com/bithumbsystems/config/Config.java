package com.bithumbsystems.config;

import lombok.Getter;

@Getter
public class Config {
    private final String baseMessage;
    private final boolean preLogger;
    private final boolean postLogger;
    private final String apiType;

    public Config(String baseMessage, boolean preLogger, boolean postLogger, String apiType) {
        this.baseMessage = baseMessage;
        this.preLogger = preLogger;
        this.postLogger = postLogger;
        this.apiType = apiType;
    }
}
