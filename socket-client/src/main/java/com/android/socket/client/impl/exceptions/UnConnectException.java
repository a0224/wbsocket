package com.android.socket.client.impl.exceptions;

public class UnConnectException extends RuntimeException {
    public UnConnectException() {
        super();
    }

    public UnConnectException(String message) {
        super(message);
    }

    public UnConnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnConnectException(Throwable cause) {
        super(cause);
    }

}
