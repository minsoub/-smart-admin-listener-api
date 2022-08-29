package com.bithumbsystems.model.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    UNKNOWN_ERROR(999, "알 수 없는 에러가 발생하였습니다. 운영자에게 문의 주시기 바랍니다!!!"),
    INVALID_API_URL(901, "Invalid API URL"),
    GATEWAY_SERVER_ERROR(910, "Gateway Server Error"),
    SERVER_RESPONSE_ERROR(920, "API Server Error.");

    private final int code;
    private final String message;

    public static ErrorCode findByCode(int code) {
        return Arrays.stream(ErrorCode.values())
                .filter(errorCode -> errorCode.hasCode(code))
                .findAny()
                .orElse(UNKNOWN_ERROR);
    }

    public static ErrorCode findByName(String message) {
        return Arrays.stream(ErrorCode.values())
                .filter(errorName -> errorName.hasMessage(message))
                .findAny()
                .orElse(UNKNOWN_ERROR);
    }

    public boolean hasCode(int code) {
        return Arrays.stream(ErrorCode.values()).anyMatch(errorCode -> errorCode.code == code);
    }
    public boolean hasMessage(String message) {
        return Arrays.stream(ErrorCode.values()).anyMatch(errorCode -> errorCode.message.equals(message));
    }
}
