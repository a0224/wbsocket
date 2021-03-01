package com.android.socket.client.impl;

import com.android.socket.client.core.interfaces.IHeartbeatSendable;
import com.android.socket.client.impl.exceptions.HeartbeatDeadException;
import com.android.socket.client.sdk.client.BSocketOptions;
import com.android.socket.client.sdk.client.bean.IHeartbeat;
import com.android.socket.client.sdk.client.connection.IConnectionManager;
import com.android.socket.client.common.base.AbsLoopThread;

import java.util.concurrent.atomic.AtomicInteger;

public class HeartbeatManager implements IHeartbeat {

    private volatile IConnectionManager mManager;

    private IHeartbeatSendable mSendable;

    private volatile BSocketOptions mOkOptions;

    private volatile long mCurrentFrequency;

    private volatile BSocketOptions.IOThreadMode mCurrentThreadMode;

    private volatile boolean isDead = false;

    private volatile AtomicInteger mLoseTimes = new AtomicInteger(-1);

    private PulseThread mPulseThread = new PulseThread();

    HeartbeatManager(IConnectionManager manager, BSocketOptions okOptions) {
        mManager = manager;
        mOkOptions = okOptions;
        mCurrentThreadMode = mOkOptions.getIOThreadMode();
    }

    public synchronized IHeartbeat setPulseSendable(IHeartbeatSendable sendable) {
        if (sendable != null) {
            mSendable = sendable;
        }
        return this;
    }

    public IHeartbeatSendable getPulseSendable() {
        return mSendable;
    }

    @Override
    public synchronized void start() {
        privateDead();
        updateFrequency();
        if (mCurrentThreadMode != BSocketOptions.IOThreadMode.SIMPLEX) {
            if (mPulseThread.isShutdown()) {
                mPulseThread.start();
            }
        }
    }

    @Override
    public synchronized void trigger() {
        if (isDead) {
            return;
        }
        if (mCurrentThreadMode != BSocketOptions.IOThreadMode.SIMPLEX && mManager != null && mSendable != null) {
            mManager.send(mSendable);
        }
    }

    public synchronized void dead() {
        mLoseTimes.set(0);
        isDead = true;
        privateDead();
    }

    private synchronized void updateFrequency() {
        if (mCurrentThreadMode != BSocketOptions.IOThreadMode.SIMPLEX) {
            mCurrentFrequency = mOkOptions.getPulseFrequency();
            mCurrentFrequency = mCurrentFrequency < 1000 ? 1000 : mCurrentFrequency;//间隔最小为一秒
        } else {
            privateDead();
        }
    }

    @Override
    public synchronized void feed() {
        mLoseTimes.set(-1);
    }

    private void privateDead() {
        if (mPulseThread != null) {
            mPulseThread.shutdown();
        }
    }

    public int getLoseTimes() {
        return mLoseTimes.get();
    }

    protected synchronized void setOkOptions(BSocketOptions okOptions) {
        mOkOptions = okOptions;
        mCurrentThreadMode = mOkOptions.getIOThreadMode();
        updateFrequency();
    }

    private class PulseThread extends AbsLoopThread {

        @Override
        protected void runInLoopThread() throws Exception {
            if (isDead) {
                shutdown();
                return;
            }
            if (mManager != null && mSendable != null) {
                if (mOkOptions.getPulseFeedLoseTimes() != -1 && mLoseTimes.incrementAndGet() >= mOkOptions.getPulseFeedLoseTimes()) {
                    mManager.disconnect(new HeartbeatDeadException("you need feed dog on time,otherwise he will die"));
                } else {
                    mManager.send(mSendable);
                }
            }

            //not safety sleep.
            Thread.sleep(mCurrentFrequency);
        }

        @Override
        protected void loopFinish(Exception e) {
        }
    }


}
