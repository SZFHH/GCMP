package com.haha.gcmp.exception;

import org.springframework.http.HttpStatus;

/**
 * Forbidden exception
 *
 * @author SZFHH
 * @date 2020/10/23
 */
public class ForbiddenException extends AbstractGcmpException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
