package com.android.socket.client.sdk.client.connection;


import com.android.socket.client.sdk.client.BSocketOptions;

public interface IConfiguration {

    IConnectionManager option(BSocketOptions okOptions);

    BSocketOptions getOption();
}
