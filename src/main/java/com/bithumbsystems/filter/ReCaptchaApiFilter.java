package com.bithumbsystems.filter;

import com.bithumbsystems.config.Config;
import com.bithumbsystems.exception.GatewayException;
import com.bithumbsystems.exception.GatewayExceptionHandler;
import com.bithumbsystems.model.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class ReCaptchaApiFilter extends AbstractGatewayFilterFactory<Config> {

  @Value("${sites.recaptcha-url}")
  private String recaptchaUrl;

  public ReCaptchaApiFilter() {
    super(Config.class);
  }

  @Bean
  public ErrorWebExceptionHandler exceptionHandler() {
    return new GatewayExceptionHandler();
  }

  @Override
  public GatewayFilter apply(final Config config) {
    return (exchange, chain) -> {
      log.info("ApiFilter called...");
      log.info("ApiFilter baseMessage: {}", config.getBaseMessage());

      if (config.isPreLogger()) {
        log.info("ApiFilter Start: {}", exchange.getRequest());
      }

      ServerHttpRequest request = exchange.getRequest();
      log.debug("header => {}", request.getHeaders());

      log.debug("URI : {}", exchange.getRequest().getURI());
      log.debug("HOST : {}", exchange.getRequest().getURI().getHost());
      log.debug("PATH : {}", exchange.getRequest().getURI().getPath());
      log.debug("RAW-QUERY : {}", exchange.getRequest().getURI().getRawQuery());
      log.debug("RAW-TYPE : {}", exchange.getRequest().getURI().getRawPath());

      AtomicReference<String> goUrl = new AtomicReference<>(recaptchaUrl);
      log.debug("goUrl:{}", goUrl.get());

      URI uri = URI.create(goUrl.get());

      ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
              .uri(uri)
              .build();
      return chain.filter(exchange.mutate().request(serverHttpRequest).build()).doOnError(e -> {
        log.error(e.getMessage());
        throw new GatewayException(ErrorCode.SERVER_RESPONSE_ERROR);
      });
    };
  }
}
