package com.everyday.api.domain.mission.exception;


import com.everyday.api.global.exception.BaseException;
import com.everyday.api.global.exception.BaseExceptionType;

public class MissionException extends BaseException {

    private BaseExceptionType baseExceptionType;


    public MissionException(BaseExceptionType baseExceptionType) {
        this.baseExceptionType = baseExceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return this.baseExceptionType;
    }
}
