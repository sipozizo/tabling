package sipozizo.tabling.common.exception.base;

import sipozizo.tabling.common.exception.ErrorCode;

public class InvalidRequestException extends BusinessException {

    public InvalidRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
