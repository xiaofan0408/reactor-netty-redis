package com.xiaofan0408.common.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;


public interface ClientMessage<T> {

  T getMessage();

  ByteBuf encode(ByteBufAllocator byteBufAllocator);
}
