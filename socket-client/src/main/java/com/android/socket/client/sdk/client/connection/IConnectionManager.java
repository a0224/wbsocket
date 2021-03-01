package com.android.socket.client.sdk.client.connection;

import com.android.socket.client.impl.HeartbeatManager;
import com.android.socket.client.sdk.client.ConnectionInfo;
import com.android.socket.client.sdk.client.action.IActionListener;
import com.android.socket.client.common.interfacies.client.IDisConnectable;
import com.android.socket.client.common.interfacies.client.ISender;
import com.android.socket.client.common.interfacies.dispatcher.IRegister;

public interface IConnectionManager extends
        IConfiguration,
        IConnectable,
        IDisConnectable,
        ISender<IConnectionManager>,
        IRegister<IActionListener, IConnectionManager> {

    boolean isConnect();

    boolean isDisconnecting();

    HeartbeatManager getPulseManager();

    void setIsConnectionHolder(boolean isHold);

    ConnectionInfo getRemoteConnectionInfo();

    ConnectionInfo getLocalConnectionInfo();

    void setLocalConnectionInfo(ConnectionInfo localConnectionInfo);

    void switchConnectionInfo(ConnectionInfo info);

}

