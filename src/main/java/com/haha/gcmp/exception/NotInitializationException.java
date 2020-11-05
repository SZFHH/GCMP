package com.haha.gcmp.exception;

/**
 * not initialization exception
 *
 * @author SZFHH
 * @date 2020/10/23
 */
public class NotInitializationException extends BadRequestException {

    public NotInitializationException(String message) {
        super(message);
    }

    public NotInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
