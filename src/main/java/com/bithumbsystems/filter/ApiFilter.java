package com.bithumbsystems.filter;

import com.bithumbsystems.config.Config;
import com.bithumbsystems.config.constant.GlobalConstant;
import com.bithumbsystems.exception.GatewayException;
import com.bithumbsystems.exception.GatewayExceptionHandler;
import com.bithumbsystems.model.enums.ErrorCode;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class ApiFilter extends AbstractGatewayFilterFactory<Config> {

  @Value("${sites.mgw-url}")
  private String mgwUrl;
  @Value("${sites.recaptcha-url}")
  private String recaptchaUrl;

  public ApiFilter() {
    super(Config.class);
  }

  @Bean
  public ErrorWebExceptionHandler exceptionHandler() {
    return new GatewayExceptionHandler();
  }

  public WebClient getWebClient(String url) {
    ConnectionProvider provider = ConnectionProvider.builder("fixed")
        .maxIdleTime(Duration.ofSeconds(20))
        .maxLifeTime(Duration.ofSeconds(600))
        .pendingAcquireTimeout(Duration.ofSeconds(60))
        .evictInBackground(Duration.ofSeconds(120)).build();

    HttpClient httpClient = HttpClient.create(provider)
        .doOnConnected(
            conn -> conn.addHandlerLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS))
                .addHandlerLast(new WriteTimeoutHandler(120, TimeUnit.SECONDS))
        ).compress(true).wiretap(true);

    return WebClient.builder()
        .baseUrl(url)
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
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

      log.debug("URI : {}", exchange.getRequest().getURI().toString());
      log.debug("HOST : {}", exchange.getRequest().getURI().getHost());
      log.debug("PATH : {}", exchange.getRequest().getURI().getPath());
      log.debug("RAW-QUERY : {}", exchange.getRequest().getURI().getRawQuery());
      log.debug("RAW-TYPE : {}", exchange.getRequest().getURI().getRawPath());

      AtomicReference<String> goUrl = getRedirectUrl(config.getApiType());



      return null;
    };
  }

  private AtomicReference<String> getRedirectUrl(String apiType) {
    AtomicReference<String> goUrl = new AtomicReference<>("");
    // Redirect URI
    if (apiType.equals(GlobalConstant.API_TYPE_MGW)) {
      goUrl.set(mgwUrl);
    } else if (apiType.equals(GlobalConstant.API_TYPE_RECAPTCHA)) {
      goUrl.set(recaptchaUrl);
    } else {
      throw new GatewayException(ErrorCode.INVALID_API_URL);
    }
    return goUrl;
  }
}
