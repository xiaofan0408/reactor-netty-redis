package com.xiaofan0408.common.command;

import com.xiaofan0408.common.core.AbstractConnection;
import com.xiaofan0408.common.message.ServerMessage;
import com.xiaofan0408.common.message.impl.PingPacket;
import com.xiaofan0408.common.message.impl.StringPacket;
import reactor.core.publisher.Flux;

public class StringCommand {

    private AbstractConnection redisConnection;

    public StringCommand(AbstractConnection redisConnection) {
        this.redisConnection = redisConnection;
    }

    public Flux<ServerMessage> ping(){
        return redisConnection.sendPacket(new PingPacket());
    }

    public Flux<ServerMessage> get(String key) {
        String command = String.format("get %s",key);
        return redisConnection.sendPacket(new StringPacket(command));
    }

    public Flux<ServerMessage> set(String key,String value) {
        String command = String.format("set %s %s",key,value);
        return redisConnection.sendPacket(new StringPacket(command));
    }
}
