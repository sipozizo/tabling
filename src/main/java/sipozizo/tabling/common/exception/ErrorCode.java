package sipozizo.tabling.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //base
    INVALID_REQUEST(HttpStatus.BAD_REQUEST,  "유효하지 않은 요청입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 엔티티입니다."),
    METHOD_NOT_AllOWED(HttpStatus.METHOD_NOT_ALLOWED,"잘못된 HTTP 메서드를 호출했습니다."),
    CONFLICT(HttpStatus.CONFLICT, "이미 존재하는 엔티티입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"서버에 에러가 발생했습니다."),

    // user
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "이메일 혹은 비밀번호가 일치하지 않습니다."),
    USER_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "접근 권한이 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 유저입니다."),

    // token
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 토큰입니다."),

    // store
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 매장입니다."),
    STORE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 매장입니다."),
    STORE_REGISTRATION_CONFLICT(HttpStatus.CONFLICT, "이미 등록된 사업자번호입니다."),
    STORE_CLOSED(HttpStatus.BAD_REQUEST, "영업시간이 아닙니다."),
    STORE_DELETED(HttpStatus.BAD_REQUEST, "폐업된 매장입니다."),

    // reservation
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다."),
    RESERVATION_COMPLETED(HttpStatus.BAD_REQUEST, "이미 완료된 예약입니다."),
    RESERVATION_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않는 예약입니다."),

    // forbidden approach
    FORBIDDEN_OPERATION(HttpStatus.BAD_REQUEST, "권한이 없습니다."),

    // server
    CACHE_CONFIGURATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "캐시 설정 오류입니다. 캐시 구성을 확인하세요.")

    ;
    private final HttpStatus status;
    private final String message;

    ErrorCode(final HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
