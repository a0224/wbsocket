package com.android.socket.client.sdk.client;

import java.net.Socket;

public abstract class BSocketFactory {

    public abstract Socket createSocket(ConnectionInfo info, BSocketOptions options) throws Exception;
}
