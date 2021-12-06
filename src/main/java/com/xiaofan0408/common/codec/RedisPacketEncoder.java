package com.xiaofan0408.common.codec;


import com.xiaofan0408.common.message.ClientMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class RedisPacketEncoder extends MessageToByteEncoder<ClientMessage> {


  @Override
  protected void encode(ChannelHandlerContext ctx, ClientMessage msg, ByteBuf out) throws Exception {
    ByteBuf buf = null;
    try {
      buf = msg.encode(ctx.alloc());
      // single mysql packet
      if (buf.writerIndex() - buf.readerIndex() < 0xffffff) {
        out.writeBytes(buf);
        //        buf.release();
        return;
      }

    } finally {
      if (buf != null) buf.release();
    }
  }

}
