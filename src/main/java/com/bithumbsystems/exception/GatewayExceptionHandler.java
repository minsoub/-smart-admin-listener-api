package com.bithumbsystems.exception;

import com.bithumbsystems.model.enums.ErrorCode;
import com.bithumbsystems.model.response.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.warn("in GATEWAY Exception handler : " + ex);
        ErrorData errorData;
        int errorCode;
        String errorMessage;

        if (ex.getClass() == GatewayException.class) {
            GatewayException e = (GatewayException) ex;
            errorCode = ErrorCode.valueOf(e.getMessage()).getCode();
            errorMessage = ErrorCode.valueOf(e.getMessage()).getMessage();
            errorData = ErrorData.builder().code(errorCode).message(errorMessage).build();
        }else if(ex.getClass() == GatewayStatusException.class){
            try {
                String err = ex.getMessage();
                String[] arr = err.split(" ");  //ex) "504 GATEWAY_TIMEOUT" 형식. 공백으로 분리
                errorCode = Integer.parseInt(arr[0]);
                errorMessage = arr[1];
                errorData = ErrorData.builder().code(errorCode).message(errorMessage).build();
            }catch(RuntimeException e){
                throw new GatewayException(ErrorCode.GATEWAY_SERVER_ERROR);
            }
        } else {
            errorData = new ErrorData(ErrorCode.UNKNOWN_ERROR);
        }

        byte[] bytes;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            bytes = (objectMapper.writeValueAsString(new ErrorResponse(errorData))).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            throw new GatewayException(ErrorCode.GATEWAY_SERVER_ERROR);
        }
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        if(ex.getClass() == GatewayStatusException.class) {
            exchange.getResponse().setRawStatusCode(errorData.getCode());
        }else {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        }
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }
}