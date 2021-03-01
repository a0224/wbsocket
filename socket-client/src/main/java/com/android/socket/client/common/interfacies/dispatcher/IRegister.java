package com.android.socket.client.common.interfacies.dispatcher;

public interface IRegister<T, E> {

    E registerReceiver(T socketActionListener);

    E unRegisterReceiver(T socketActionListener);
}
