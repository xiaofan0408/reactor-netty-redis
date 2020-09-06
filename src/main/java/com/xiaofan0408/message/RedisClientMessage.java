package com.xiaofan0408.message;

import com.xiaofan0408.codec.RedisCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public abstract class RedisClientMessage implements ClientMessage<String>{

    @Override
    public ByteBuf encode(ByteBufAllocator byteBufAllocator) {
        ByteBuf buf = byteBufAllocator.ioBuffer();
        buf.writeBytes(RedisCodec.translateCommand(getMessage()));
        return buf;
    }
}
