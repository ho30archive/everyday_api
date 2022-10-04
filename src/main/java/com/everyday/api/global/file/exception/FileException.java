package com.everyday.api.global.file.exception;

import com.everyday.api.global.exception.BaseException;
import com.everyday.api.global.exception.BaseExceptionType;

public class FileException extends BaseException {
    private BaseExceptionType exceptionType;


    public FileException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}