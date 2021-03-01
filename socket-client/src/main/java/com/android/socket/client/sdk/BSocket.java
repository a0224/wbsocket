package com.android.socket.client.sdk;


import com.android.socket.client.impl.SocketHolder;
import com.android.socket.client.sdk.client.BSocketOptions;
import com.android.socket.client.sdk.client.ConnectionInfo;
import com.android.socket.client.sdk.client.connection.IConnectionManager;
import com.android.socket.client.common.interfacies.dispatcher.IRegister;
import com.android.socket.client.common.interfacies.server.IServerActionListener;
import com.android.socket.client.common.interfacies.server.IServerManager;

public class BSocket {

    private static SocketHolder holder = SocketHolder.getInstance();

    public static IRegister<IServerActionListener, IServerManager> server(int serverPort) {
        return (IRegister<IServerActionListener, IServerManager>) holder.getServer(serverPort);
    }

    public static IConnectionManager open(ConnectionInfo connectInfo) {
        return holder.getConnection(connectInfo);
    }

    public static IConnectionManager open(String ip, int port) {
        ConnectionInfo info = new ConnectionInfo(ip, port);
        return holder.getConnection(info);
    }

    public static IConnectionManager open(ConnectionInfo connectInfo, BSocketOptions okOptions) {
        return holder.getConnection(connectInfo, okOptions);
    }

    public static IConnectionManager open(String ip, int port, BSocketOptions okOptions) {
        ConnectionInfo info = new ConnectionInfo(ip, port);
        return holder.getConnection(info, okOptions);
    }
}
