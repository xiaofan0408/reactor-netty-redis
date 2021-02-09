package com.xiaofan0408.common.message.impl;

import com.xiaofan0408.common.message.RedisClientMessage;

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
