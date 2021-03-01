package com.android.socket.client.sdk.client;

import java.io.Serializable;

public final class ConnectionInfo implements Serializable, Cloneable {

    private String mIp;

    private int mPort;

    private ConnectionInfo mBackupInfo;

    public ConnectionInfo(String ip, int port) {
        this.mIp = ip;
        this.mPort = port;
    }

    public String getIp() {
        return mIp;
    }

    public int getPort() {
        return mPort;
    }

    public ConnectionInfo getBackupInfo() {
        return mBackupInfo;
    }

    public void setBackupInfo(ConnectionInfo backupInfo) {
        mBackupInfo = backupInfo;
    }

    @Override
    public ConnectionInfo clone() {
        ConnectionInfo connectionInfo = new ConnectionInfo(mIp, mPort);
        if (mBackupInfo != null) {
            connectionInfo.setBackupInfo(mBackupInfo.clone());
        }
        return connectionInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof ConnectionInfo)) { return false; }

        ConnectionInfo connectInfo = (ConnectionInfo) o;

        if (mPort != connectInfo.mPort) { return false; }
        return mIp.equals(connectInfo.mIp);
    }

    @Override
    public int hashCode() {
        int result = mIp.hashCode();
        result = 31 * result + mPort;
        return result;
    }
}
