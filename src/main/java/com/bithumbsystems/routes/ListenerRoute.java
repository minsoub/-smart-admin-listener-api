package com.bithumbsystems.routes;

import com.bithumbsystems.config.Config;
import com.bithumbsystems.config.properties.UrlProperties;
import com.bithumbsystems.filter.ReCaptchaApiFilter;
import com.bithumbsystems.filter.SlackApiFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class ListenerRoute {

    private final UrlProperties urlProperties;
    private final ReCaptchaApiFilter reCaptchaApiFilter;
    private final SlackApiFilter slackApiFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("recaptcha-filter",   // 구글 recaptcha
                        route -> route.path("/recaptcha/**")
                                .filters(filter -> filter.filter(reCaptchaApiFilter.apply(new Config("ReCaptcha apply"))))
                                .uri(urlProperties.getRecaptchaUrl())
                )
                .route("slack-filter",   // 슬렉
                        route -> route.path("/slack/**")
                                .filters(filter -> filter.filter(slackApiFilter.apply(new Config("Slack apply"))))
                                .uri(urlProperties.getSlackUrl())
                )
                .build();
    }
}
