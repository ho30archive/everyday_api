package com.everyday.api.domain.mission.exception;

import com.everyday.api.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum MissionExceptionType implements BaseExceptionType {

    MISSION_NOT_POUND(700, HttpStatus.NOT_FOUND, "찾으시는 미션이 없습니다"),
    NOT_AUTHORITY_UPDATE_MISSION(701, HttpStatus.FORBIDDEN, "미션을 업데이트할 권한이 없습니다."),
    NOT_AUTHORITY_DELETE_MISSION(702, HttpStatus.FORBIDDEN, "미션을 삭제할 권한이 없습니다.");


    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;

    MissionExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }


    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}

