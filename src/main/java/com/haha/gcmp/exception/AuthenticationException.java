package com.haha.gcmp.exception;

import org.springframework.http.HttpStatus;

/**
 * Authentication exception.
 *
 * @author johnniang
 */
public class AuthenticationException extends AbstractGcmpException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
