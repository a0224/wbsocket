package com.android.socket.client.common.interfacies;


import com.android.socket.client.core.interfaces.IIOCoreOptions;
import com.android.socket.client.core.interfaces.ISendable;

public interface IIOManager<E extends IIOCoreOptions> {
    void startEngine();

    void setOkOptions(E options);

    void send(ISendable sendable);

    void close();

    void close(Exception e);

}
