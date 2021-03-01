package com.android.socket.client.sdk.client.bean;

public interface IHeartbeat {

    void start();

    void trigger();

    void dead();

    void feed();
}

