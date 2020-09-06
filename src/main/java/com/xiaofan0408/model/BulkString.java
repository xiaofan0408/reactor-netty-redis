package com.xiaofan0408.model;


import java.util.Arrays;

public class BulkString implements RedisObject {

    public BulkString(byte[] bytes) {
        this.bytes = bytes;
    }

    private final byte[] bytes;

    @Override
    public Type getType() {
        return Type.BULK_STRING;
    }

    @Override
    public String toString() {
        return new String(bytes);
    }
}
