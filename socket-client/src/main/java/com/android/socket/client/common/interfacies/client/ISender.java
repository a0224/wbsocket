package com.android.socket.client.common.interfacies.client;

import com.android.socket.client.core.interfaces.ISendable;

public interface ISender<T> {

    T send(ISendable sendable);
}
