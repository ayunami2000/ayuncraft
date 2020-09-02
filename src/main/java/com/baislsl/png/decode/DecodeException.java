package com.baislsl.png.decode;

/**
 * Created by baislsl on 17-7-9.
 */
public class DecodeException extends Exception {

    public DecodeException() {}

    public DecodeException(String message) {
        super(message);
    }

    public DecodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecodeException(Throwable cause) {
        super(cause);
    }
}
