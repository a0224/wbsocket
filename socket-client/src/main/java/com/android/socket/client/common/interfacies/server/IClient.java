package com.android.socket.client.common.interfacies.server;

import com.android.socket.client.core.IReaderData;
import com.android.socket.client.common.interfacies.client.IDisConnectable;
import com.android.socket.client.common.interfacies.client.ISender;

import java.io.Serializable;

public interface IClient extends IDisConnectable, ISender<IClient>, Serializable {

    String getHostIp();

    String getHostName();

    String getUniqueTag();

    void setReaderProtocol(IReaderData protocol);

    void addIOCallback(IClientIOCallback clientIOCallback);

    void removeIOCallback(IClientIOCallback clientIOCallback);

    void removeAllIOCallback();

}
