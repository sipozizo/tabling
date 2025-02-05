package sipozizo.tabling.common.exception.base;

import sipozizo.tabling.common.exception.ErrorCode;

public class ServerException extends BusinessException {

    public ServerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
