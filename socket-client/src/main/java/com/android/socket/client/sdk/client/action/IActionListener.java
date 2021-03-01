package com.android.socket.client.sdk.client.action;


import com.android.socket.client.core.interfaces.ISendable;
import com.android.socket.client.core.OriginalData;
import com.android.socket.client.core.interfaces.IHeartbeatSendable;
import com.android.socket.client.sdk.client.ConnectionInfo;

public interface IActionListener {

    void onSocketIOThreadStart(String action);

    void onSocketIOThreadShutdown(String action, Exception e);

    void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data);

    void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data);

    void onPulseSend(ConnectionInfo info, IHeartbeatSendable data);

    void onSocketDisconnection(ConnectionInfo info, String action, Exception e);

    void onSocketConnectionSuccess(ConnectionInfo info, String action);

    void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e);
}
