package com.bithumbsystems.filter;

import static com.bithumbsystems.GlobalConstant.XANGLE_HEADER_KEY_NAME;
import static com.bithumbsystems.GlobalConstant.XANGLE_KEY_NAME;

import com.bithumbsystems.config.Config;
import com.bithumbsystems.exception.GatewayException;
import com.bithumbsystems.model.enums.ErrorCode;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Slf4j
@Component
public class XangleApiFilter extends AbstractGatewayFilterFactory<Config> {

  @Value("${sites.xangle-url}")
  private String xangleUrl;

  public XangleApiFilter() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(final Config config) {
    return (exchange, chain) -> {
      log.info("ApiFilter called...");
      log.info("ApiFilter baseMessage: {}", config.getBaseMessage());

      ServerHttpRequest request = exchange.getRequest();
      log.debug("header => {}", request.getHeaders());

      log.debug("URI : {}", exchange.getRequest().getURI());
      log.debug("HOST : {}", exchange.getRequest().getURI().getHost());
      log.debug("PATH : {}", exchange.getRequest().getURI().getPath());
      log.debug("RAW-QUERY : {}", exchange.getRequest().getURI().getRawQuery());
      log.debug("RAW-TYPE : {}", exchange.getRequest().getURI().getRawPath());

      AtomicReference<String> goUrl = new AtomicReference<>(xangleUrl);
      log.debug("goUrl:{}", goUrl.get());

      String pathOrigin = exchange.getRequest().getURI().getPath();
      String path = pathOrigin.replaceAll("/xangle", "");

      log.debug("PATH:{}:{}", pathOrigin, path);

      String replaceUrl = goUrl.get() + path;
      if (StringUtils.hasLength(exchange.getRequest().getURI().getQuery())) {
        replaceUrl += "?" + exchange.getRequest().getURI().getRawQuery();
      }
      log.debug("replaceUrl:" + replaceUrl);

      URI uri = URI.create(replaceUrl);

      ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
          .uri(uri)
          .header(XANGLE_KEY_NAME, request.getHeaders().get(XANGLE_HEADER_KEY_NAME).get(0))
          .build();

      return chain.filter(exchange.mutate().request(serverHttpRequest).build()).doOnError(e -> {
        log.error(e.getMessage());
        throw new GatewayException(ErrorCode.SERVER_RESPONSE_ERROR);
      });
    };
  }
}