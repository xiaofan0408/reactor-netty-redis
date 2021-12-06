package com.xiaofan0408.common.message.impl;

import com.xiaofan0408.common.message.RedisClientMessage;

/**
 * @author zefan.xzf
 * @date 2021/12/6 16:05
 */
public class ExistsPacket extends RedisClientMessage {

    private String key;

    public ExistsPacket(String key) {
        this.key = key;
    }

    @Override
    public String getMessage() {
        return "exists " + key;
    }
}
