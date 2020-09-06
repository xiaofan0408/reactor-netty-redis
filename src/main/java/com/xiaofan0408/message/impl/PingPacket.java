package com.xiaofan0408.message.impl;

import com.xiaofan0408.message.RedisClientMessage;


/**
 * @author xuzefan  2020/9/4 14:55
 */
public class PingPacket extends RedisClientMessage {

    @Override
    public String getMessage() {
        return "ping";
    }
}
