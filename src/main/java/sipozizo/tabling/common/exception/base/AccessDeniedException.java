package sipozizo.tabling.common.exception.base;

import sipozizo.tabling.common.exception.ErrorCode;

public class AccessDeniedException extends BusinessException {

    public AccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
