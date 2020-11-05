package com.haha.gcmp.exception;

import org.springframework.http.HttpStatus;

/**
 * not found exception
 *
 * @author SZFHH
 * @date 2020/10/23
 */
public class NotFoundException extends AbstractGcmpException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
