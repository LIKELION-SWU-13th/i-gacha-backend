package team.backend.apiPayload.exception.handler;

import team.backend.apiPayload.code.BaseErrorCode;
import team.backend.apiPayload.exception.GeneralException;

public class EventHandler extends GeneralException {
    public EventHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
