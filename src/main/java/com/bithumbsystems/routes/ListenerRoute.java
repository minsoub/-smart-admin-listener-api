package com.bithumbsystems.routes;

import com.bithumbsystems.config.Config;
import com.bithumbsystems.config.properties.UrlProperties;
import com.bithumbsystems.filter.ReCaptchaApiFilter;
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

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("recaptcha-filter",   // 구글 recaptcha
                        route -> route.path("/recaptcha/**")
                                .filters(filter -> filter.filter(reCaptchaApiFilter.apply(new Config("ReCaptcha apply", true, true))))
                                .uri(urlProperties.getRecaptchaUrl())
                )
                .route("messenger-filter",   // 다우메신저(모바일 그룹웨어)
                        route -> route.path("/messenger/**")
                                .filters(filter -> filter.filter(reCaptchaApiFilter.apply(new Config("Messenser apply", true, true))))
                                .uri(urlProperties.getRecaptchaUrl())
                )
                .build();
    }
}