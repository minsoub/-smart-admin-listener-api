package com.bithumbsystems.routes;

import com.bithumbsystems.config.Config;
import com.bithumbsystems.config.constant.GlobalConstant;
import com.bithumbsystems.config.properties.UrlProperties;
import com.bithumbsystems.filter.ApiFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class UserAuthRoute {

    private final UrlProperties urlProperties;
    private final ApiFilter apiFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("recaptcha-filter",   // 구글 recaptcha
                        route -> route.path("/recaptcha/**")
                                .filters(filter -> filter.filter(apiFilter.apply(new Config("ApiFilter apply", true, true, GlobalConstant.API_TYPE_RECAPTCHA))))
                                .uri(urlProperties.getRecaptchaUrl())
                )
                .route("mgw-filter",   // 모바일 그룹웨어(다우메신저)
                        route -> route.path("/recaptcha/**")
                                .filters(filter -> filter.filter(apiFilter.apply(new Config("ApiFilter apply", true, true, GlobalConstant.API_TYPE_MGW))))
                                .uri(urlProperties.getRecaptchaUrl())
                )
                .build();
    }
}
