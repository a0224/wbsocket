package com.android.socket.client.impl;


import com.android.socket.client.impl.action.ActionDispatcher;
import com.android.socket.client.impl.abilities.IConnectionSwitchListener;
import com.android.socket.client.sdk.client.ConnectionInfo;
import com.android.socket.client.sdk.client.action.IActionListener;
import com.android.socket.client.sdk.client.connection.IConnectionManager;

import java.io.Serializable;


public abstract class AbsConnectionManager implements IConnectionManager {

    protected ConnectionInfo mRemoteConnectionInfo;

    protected ConnectionInfo mLocalConnectionInfo;

    private IConnectionSwitchListener mConnectionSwitchListener;

    protected ActionDispatcher mActionDispatcher;

    public AbsConnectionManager(ConnectionInfo info) {
        this(info, null);
    }

    public AbsConnectionManager(ConnectionInfo remoteInfo, ConnectionInfo localInfo) {
        mRemoteConnectionInfo = remoteInfo;
        mLocalConnectionInfo = localInfo;
        mActionDispatcher = new ActionDispatcher(remoteInfo, this);
    }

    public IConnectionManager registerReceiver(final IActionListener socketResponseHandler) {
        mActionDispatcher.registerReceiver(socketResponseHandler);
        return this;
    }

    public IConnectionManager unRegisterReceiver(IActionListener socketResponseHandler) {
        mActionDispatcher.unRegisterReceiver(socketResponseHandler);
        return this;
    }

    protected void sendBroadcast(String action, Serializable serializable) {
        mActionDispatcher.sendBroadcast(action, serializable);
    }

    protected void sendBroadcast(String action) {
        mActionDispatcher.sendBroadcast(action);
    }

    @Override
    public ConnectionInfo getRemoteConnectionInfo() {
        if (mRemoteConnectionInfo != null) {
            return mRemoteConnectionInfo.clone();
        }
        return null;
    }

    @Override
    public ConnectionInfo getLocalConnectionInfo() {
        if (mLocalConnectionInfo != null) {
            return mLocalConnectionInfo;
        }
        return null;
    }

    @Override
    public synchronized void switchConnectionInfo(ConnectionInfo info) {
        if (info != null) {
            ConnectionInfo tempOldInfo = mRemoteConnectionInfo;
            mRemoteConnectionInfo = info.clone();
            if (mActionDispatcher != null) {
                mActionDispatcher.setConnectionInfo(mRemoteConnectionInfo);
            }
            if (mConnectionSwitchListener != null) {
                mConnectionSwitchListener.onSwitchConnectionInfo(this, tempOldInfo, mRemoteConnectionInfo);
            }
        }
    }

    protected void setOnConnectionSwitchListener(IConnectionSwitchListener listener) {
        mConnectionSwitchListener = listener;
    }

}
