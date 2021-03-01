package com.android.socket.client.sdk.client.action;

import com.android.socket.client.core.interfaces.ISendable;
import com.android.socket.client.core.OriginalData;
import com.android.socket.client.core.interfaces.IHeartbeatSendable;
import com.android.socket.client.sdk.client.ConnectionInfo;

public abstract class SocketActionAdapter implements IActionListener {

    @Override
    public void onSocketIOThreadStart(String action) {

    }

    @Override
    public void onSocketIOThreadShutdown(String action, Exception e) {

    }

    @Override
    public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {

    }

    @Override
    public void onSocketConnectionSuccess(ConnectionInfo info, String action) {

    }

    @Override
    public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {

    }

    @Override
    public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {

    }

    @Override
    public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {

    }

    @Override
    public void onPulseSend(ConnectionInfo info, IHeartbeatSendable data) {

    }
}
