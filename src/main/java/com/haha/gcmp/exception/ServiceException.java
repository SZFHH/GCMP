package com.haha.gcmp.exception;

import org.springframework.http.HttpStatus;

/**
 * service exception
 *
 * @author SZFHH
 * @date 2020/10/23
 */
public class ServiceException extends AbstractGcmpException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
