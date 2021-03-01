package com.android.socket.client.common.interfacies.server;


import com.android.socket.client.core.interfaces.ISendable;
import com.android.socket.client.core.OriginalData;

public interface IClientIOCallback {

    void onClientRead(OriginalData originalData, IClient client, IClientPool<IClient, String> clientPool);

    void onClientWrite(ISendable sendable, IClient client, IClientPool<IClient, String> clientPool);

}
