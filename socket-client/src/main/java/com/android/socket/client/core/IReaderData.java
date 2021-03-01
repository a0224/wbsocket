package com.android.socket.client.core;

import java.nio.ByteOrder;

public interface IReaderData {

    int getHeaderLength();

    int getBodyLength(byte[] header, ByteOrder byteOrder);
}
