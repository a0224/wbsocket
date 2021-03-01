package com.android.socket.client.impl.abilities;


import com.android.socket.client.sdk.client.ConnectionInfo;
import com.android.socket.client.sdk.client.connection.IConnectionManager;

public interface IConnectionSwitchListener {
    void onSwitchConnectionInfo(IConnectionManager manager, ConnectionInfo oldInfo, ConnectionInfo newInfo);
}
