package com.xiaofan0408.command;

import com.xiaofan0408.core.RedisConnection;
import com.xiaofan0408.message.ServerMessage;
import com.xiaofan0408.message.impl.PingPacket;
import com.xiaofan0408.message.impl.StringPacket;
import reactor.core.publisher.Flux;

public class StringCommand {

    private RedisConnection redisConnection;

    public StringCommand(RedisConnection redisConnection) {
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
