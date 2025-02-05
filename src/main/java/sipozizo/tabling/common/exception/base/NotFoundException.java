package sipozizo.tabling.common.exception.base;

import sipozizo.tabling.common.exception.ErrorCode;

public class NotFoundException extends BusinessException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
