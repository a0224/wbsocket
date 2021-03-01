package com.android.socket.client.impl.exceptions;

public class HeartbeatDeadException extends RuntimeException {
    public HeartbeatDeadException() {
        super();
    }

    public HeartbeatDeadException(String message) {
        super(message);
    }

    public HeartbeatDeadException(String message, Throwable cause) {
        super(message, cause);
    }

    public HeartbeatDeadException(Throwable cause) {
        super(cause);
    }

}
