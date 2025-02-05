package sipozizo.tabling.common.exception.base;

import sipozizo.tabling.common.exception.ErrorCode;

public class AuthException extends BusinessException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
