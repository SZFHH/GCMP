package com.haha.gcmp.exception;

/**
 * Not install exception.
 *
 * @author johnniang
 * @date 19-4-29
 */
public class NotInitializationException extends BadRequestException {

    public NotInitializationException(String message) {
        super(message);
    }

    public NotInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
