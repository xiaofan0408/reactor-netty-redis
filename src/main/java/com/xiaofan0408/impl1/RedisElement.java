package com.xiaofan0408.impl1;

import com.xiaofan0408.common.message.ServerMessage;
import reactor.core.publisher.FluxSink;

/**
 * @author xuzefan  2020/9/4 14:17
 */
public class RedisElement {
    private final FluxSink<ServerMessage> sink;
    private final Object command;

    public RedisElement(FluxSink<ServerMessage> sink) {
        this.sink = sink;
        this.command = null;
    }

    public RedisElement(FluxSink<ServerMessage> sink, Object command) {
        this.sink = sink;
        this.command = command;
    }

    public FluxSink<ServerMessage> getSink() {
        return sink;
    }


    public Object getCommand() {
        return command;
    }
}
