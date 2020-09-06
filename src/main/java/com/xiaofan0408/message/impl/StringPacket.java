package com.xiaofan0408.message.impl;

import com.xiaofan0408.message.RedisClientMessage;

public class StringPacket extends RedisClientMessage {

    private String message;

    public StringPacket(String key) {
        this.message = key;
    }


    @Override
    public String getMessage() {
        return message;
    }
}
