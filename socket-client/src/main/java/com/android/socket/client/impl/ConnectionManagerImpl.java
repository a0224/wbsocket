package com.android.socket.client.impl;

import com.android.socket.client.impl.action.ActionHandler;
import com.android.socket.client.core.interfaces.ISendable;
import com.android.socket.client.core.SLog;
import com.android.socket.client.impl.iothreads.IOThreadManager;
import com.android.socket.client.impl.exceptions.ManuallyDisconnectException;
import com.android.socket.client.impl.exceptions.UnConnectException;
import com.android.socket.client.sdk.client.ConnectionInfo;
import com.android.socket.client.sdk.client.BSocketOptions;
import com.android.socket.client.sdk.client.BSocketSSLConfig;
import com.android.socket.client.sdk.client.action.IAction;
import com.android.socket.client.sdk.client.connection.IConnectionManager;
import com.android.socket.client.common.interfacies.IIOManager;
import com.android.socket.client.common.protocol.SSlX509TrustManager;
import com.android.socket.client.common.utils.TextUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class ConnectionManagerImpl extends AbsConnectionManager {

    private volatile Socket mSocket;

    private volatile BSocketOptions mOptions;

    private IIOManager mManager;

    private Thread mConnectThread;

    private com.android.socket.client.impl.action.ActionHandler mActionHandler;

    private volatile HeartbeatManager mPulseManager;

    private volatile boolean isConnectionPermitted = true;

    private volatile boolean isDisconnecting = false;


    protected ConnectionManagerImpl(ConnectionInfo info) {
        this(info, null);
    }

    public ConnectionManagerImpl(ConnectionInfo remoteInfo, ConnectionInfo localInfo) {
        super(remoteInfo, localInfo);
        String ip = "";
        String port = "";
        if (remoteInfo != null) {
            ip = remoteInfo.getIp();
            port = remoteInfo.getPort() + "";
        }
        SLog.i("block connection init with:" + ip + ":" + port);

        if (localInfo != null) {
            SLog.i("binding local addr:" + localInfo.getIp() + " port:" + localInfo.getPort());
        }
    }

    @Override
    public synchronized void connect() {
        SLog.i("Thread name:" + Thread.currentThread().getName() + " id:" + Thread.currentThread().getId());
        if (!isConnectionPermitted) {
            return;
        }
        isConnectionPermitted = false;
        if (isConnect()) {
            return;
        }
        isDisconnecting = false;
        if (mRemoteConnectionInfo == null) {
            isConnectionPermitted = true;
            throw new UnConnectException("连接参数为空,检查连接参数");
        }
        if (mActionHandler != null) {
            mActionHandler.detach(this);
            SLog.i("mActionHandler is detached.");
        }
        mActionHandler = new ActionHandler();
        mActionHandler.attach(this, this);

        String info = mRemoteConnectionInfo.getIp() + ":" + mRemoteConnectionInfo.getPort();
        mConnectThread = new ConnectionThread(" Connect thread for " + info);
        mConnectThread.setDaemon(true);
        mConnectThread.start();
    }

    private synchronized Socket getSocketByConfig() throws Exception {
        //自定义socket操作
        if (mOptions.getOkSocketFactory() != null) {
            return mOptions.getOkSocketFactory().createSocket(mRemoteConnectionInfo, mOptions);
        }

        //默认操作
        BSocketSSLConfig config = mOptions.getSSLConfig();
        if (config == null) {
            return new Socket();
        }

        SSLSocketFactory factory = config.getCustomSSLFactory();
        if (factory == null) {
            String protocol = "SSL";
            if (!TextUtils.isEmpty(config.getProtocol())) {
                protocol = config.getProtocol();
            }

            TrustManager[] trustManagers = config.getTrustManagers();
            if (trustManagers == null || trustManagers.length == 0) {
                //缺省信任所有证书
                trustManagers = new TrustManager[]{new SSlX509TrustManager()};
            }

            try {
                SSLContext sslContext = SSLContext.getInstance(protocol);
                sslContext.init(config.getKeyManagers(), trustManagers, new SecureRandom());
                return sslContext.getSocketFactory().createSocket();
            } catch (Exception e) {
                if (mOptions.isDebug()) {
                    e.printStackTrace();
                }
                SLog.e(e.getMessage());
                return new Socket();
            }

        } else {
            try {
                return factory.createSocket();
            } catch (IOException e) {
                if (mOptions.isDebug()) {
                    e.printStackTrace();
                }
                SLog.e(e.getMessage());
                return new Socket();
            }
        }
    }

    private class ConnectionThread extends Thread {
        public ConnectionThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            try {
                try {
                    mSocket = getSocketByConfig();
                } catch (Exception e) {
                    if (mOptions.isDebug()) {
                        e.printStackTrace();
                    }
                    throw new UnConnectException("Create socket failed.", e);
                }
                if (mLocalConnectionInfo != null) {
                    SLog.i("try bind: " + mLocalConnectionInfo.getIp() + " port:" + mLocalConnectionInfo.getPort());
                    mSocket.bind(new InetSocketAddress(mLocalConnectionInfo.getIp(), mLocalConnectionInfo.getPort()));
                }

                SLog.i("Start connect: " + mRemoteConnectionInfo.getIp() + ":" + mRemoteConnectionInfo.getPort() + " socket server...");
                mSocket.connect(new InetSocketAddress(mRemoteConnectionInfo.getIp(), mRemoteConnectionInfo.getPort()), mOptions.getConnectTimeoutSecond() * 1000);
                //关闭Nagle算法,无论TCP数据报大小,立即发送
                mSocket.setTcpNoDelay(true);
                resolveManager();
                sendBroadcast(IAction.ACTION_CONNECTION_SUCCESS);
                SLog.i("Socket server: " + mRemoteConnectionInfo.getIp() + ":" + mRemoteConnectionInfo.getPort() + " connect successful!");
            } catch (Exception e) {
                if (mOptions.isDebug()) {
                    e.printStackTrace();
                }
                Exception exception = new UnConnectException(e);
                SLog.e("Socket server " + mRemoteConnectionInfo.getIp() + ":" + mRemoteConnectionInfo.getPort() + " connect failed! error msg:" + e.getMessage());
                sendBroadcast(IAction.ACTION_CONNECTION_FAILED, exception);
            } finally {
                isConnectionPermitted = true;
            }
        }
    }

    private void resolveManager() throws IOException {
        mPulseManager = new HeartbeatManager(this, mOptions);

        mManager = new IOThreadManager(
                mSocket.getInputStream(),
                mSocket.getOutputStream(),
                mOptions,
                mActionDispatcher);
        mManager.startEngine();
    }

    @Override
    public void disconnect(Exception exception) {
        synchronized (this) {
            if (isDisconnecting) {
                return;
            }
            isDisconnecting = true;

            if (mPulseManager != null) {
                mPulseManager.dead();
                mPulseManager = null;
            }
        }

        String info = mRemoteConnectionInfo.getIp() + ":" + mRemoteConnectionInfo.getPort();
        DisconnectThread thread = new DisconnectThread(exception, "Disconnect Thread for " + info);
        thread.setDaemon(true);
        thread.start();
    }

    private class DisconnectThread extends Thread {
        private Exception mException;

        public DisconnectThread(Exception exception, String name) {
            super(name);
            mException = exception;
        }

        @Override
        public void run() {
            try {
                if (mManager != null) {
                    mManager.close(mException);
                }

                if (mConnectThread != null && mConnectThread.isAlive()) {
                    mConnectThread.interrupt();
                    try {
                        SLog.i("disconnect thread need waiting for connection thread done.");
                        mConnectThread.join();
                    } catch (InterruptedException e) {
                    }
                    SLog.i("connection thread is done. disconnection thread going on");
                    mConnectThread = null;
                }

                if (mSocket != null) {
                    try {
                        mSocket.close();
                    } catch (IOException e) {
                    }
                }

                if (mActionHandler != null) {
                    mActionHandler.detach(ConnectionManagerImpl.this);
                    SLog.i("mActionHandler is detached.");
                    mActionHandler = null;
                }

            } finally {
                isDisconnecting = false;
                isConnectionPermitted = true;
                if (!(mException instanceof UnConnectException) && mSocket != null) {
                    mException = mException instanceof ManuallyDisconnectException ? null : mException;
                    sendBroadcast(IAction.ACTION_DISCONNECTION, mException);
                }
                mSocket = null;

                if (mException != null) {
                    SLog.e("socket is disconnecting because: " + mException.getMessage());
                    if (mOptions.isDebug()) {
                        mException.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void disconnect() {
        disconnect(new ManuallyDisconnectException());
    }

    @Override
    public IConnectionManager send(ISendable sendable) {
        if (mManager != null && sendable != null && isConnect()) {
            mManager.send(sendable);
        }
        return this;
    }

    @Override
    public IConnectionManager option(BSocketOptions okOptions) {
        if (okOptions == null) {
            return this;
        }
        mOptions = okOptions;
        if (mManager != null) {
            mManager.setOkOptions(mOptions);
        }

        if (mPulseManager != null) {
            mPulseManager.setOkOptions(mOptions);
        }

        return this;
    }

    @Override
    public BSocketOptions getOption() {
        return mOptions;
    }

    @Override
    public boolean isConnect() {
        if (mSocket == null) {
            return false;
        }

        return mSocket.isConnected() && !mSocket.isClosed();
    }

    @Override
    public boolean isDisconnecting() {
        return isDisconnecting;
    }

    @Override
    public HeartbeatManager getPulseManager() {
        return mPulseManager;
    }

    @Override
    public void setIsConnectionHolder(boolean isHold) {
        mOptions = new BSocketOptions.Builder(mOptions).setConnectionHolden(isHold).build();
    }

    @Override
    public ConnectionInfo getLocalConnectionInfo() {
        ConnectionInfo local = super.getLocalConnectionInfo();
        if (local == null) {
            if (isConnect()) {
                InetSocketAddress address = (InetSocketAddress) mSocket.getLocalSocketAddress();
                if (address != null) {
                    local = new ConnectionInfo(address.getHostName(), address.getPort());
                }
            }
        }
        return local;
    }

    @Override
    public void setLocalConnectionInfo(ConnectionInfo localConnectionInfo) {
        if (isConnect()) {
            throw new IllegalStateException("Socket is connected, can't set local info after connect.");
        }
        mLocalConnectionInfo = localConnectionInfo;
    }
}
