package com.android.socket.client.common.protocol;



import com.android.socket.client.core.IReaderData;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DefaultReaderProtocol implements IReaderData {

    @Override
    public int getHeaderLength() {
        return 4;
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
        if (header == null || header.length < getHeaderLength()) {
            return 0;
        }
        ByteBuffer bb = ByteBuffer.wrap(header);
        bb.order(byteOrder);
        return bb.getInt();
    }
}