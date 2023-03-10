package com.bithumbsystems.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sites")
@ConfigurationPropertiesScan
public class UrlProperties {
    private String recaptchaUrl;
    private String messengerUrl;
    private String telegramUrl;
    private String slackUrl;
    private String xangleUrl;
}
