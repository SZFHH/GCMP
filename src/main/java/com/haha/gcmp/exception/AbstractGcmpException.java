package com.haha.gcmp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 本项目的Base exception
 *
 * @author SZFHH
 * @date 2020/10/23
 */
public abstract class AbstractGcmpException extends RuntimeException {
    private Object errorData;

    public AbstractGcmpException(String message) {
        super(message);
    }

    public AbstractGcmpException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Http status code
     *
     * @return {@link HttpStatus}
     */
    @NonNull
    public abstract HttpStatus getStatus();

    @Nullable
    public Object getErrorData() {
        return errorData;
    }

    /**
     * Sets error errorData.
     *
     * @param errorData error data
     * @return current exception.
     */
    @NonNull
    public AbstractGcmpException setErrorData(@Nullable Object errorData) {
        this.errorData = errorData;
        return this;
    }
}
