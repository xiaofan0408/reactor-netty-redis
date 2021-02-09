package com.xiaofan0408.common.message.impl;

import com.xiaofan0408.common.message.RedisClientMessage;


/**
 * @author xuzefan  2020/9/4 14:55
 */
public class PingPacket extends RedisClientMessage {

    @Override
    public String getMessage() {
        return "ping";
    }
}
