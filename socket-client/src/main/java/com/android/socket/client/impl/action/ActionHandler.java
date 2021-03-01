package com.android.socket.client.impl.action;


import com.android.socket.client.impl.exceptions.ManuallyDisconnectException;
import com.android.socket.client.sdk.client.ConnectionInfo;
import com.android.socket.client.sdk.client.BSocketOptions;
import com.android.socket.client.sdk.client.action.IActionListener;
import com.android.socket.client.sdk.client.action.SocketActionAdapter;
import com.android.socket.client.sdk.client.connection.IConnectionManager;
import com.android.socket.client.common.interfacies.dispatcher.IRegister;

public class ActionHandler extends SocketActionAdapter {
    private IConnectionManager mManager;

    private BSocketOptions.IOThreadMode mCurrentThreadMode;

    private boolean iOThreadIsCalledDisconnect = false;

    public ActionHandler() {

    }

    public void attach(IConnectionManager manager, IRegister<IActionListener, IConnectionManager> register) {
        this.mManager = manager;
        register.registerReceiver(this);
    }

    public void detach(IRegister register) {
        register.unRegisterReceiver(this);
    }

    @Override
    public void onSocketIOThreadStart(String action) {
        if (mManager.getOption().getIOThreadMode() != mCurrentThreadMode) {
            mCurrentThreadMode = mManager.getOption().getIOThreadMode();
        }
        iOThreadIsCalledDisconnect = false;
    }

    @Override
    public void onSocketIOThreadShutdown(String action, Exception e) {
        if (mCurrentThreadMode != mManager.getOption().getIOThreadMode()) {//切换线程模式,不需要断开连接
            //do nothing
        } else {//多工模式
            if (!iOThreadIsCalledDisconnect) {//保证只调用一次,多工多线程,会调用两次
                iOThreadIsCalledDisconnect = true;
                if (!(e instanceof ManuallyDisconnectException)) {
                    mManager.disconnect(e);
                }
            }
        }
    }

    @Override
    public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
        mManager.disconnect(e);
    }
}
