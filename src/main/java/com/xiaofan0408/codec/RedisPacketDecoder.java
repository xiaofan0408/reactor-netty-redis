package com.xiaofan0408.codec;

import com.xiaofan0408.core.AbstractConnection;
import com.xiaofan0408.enu.RedisException;
import com.xiaofan0408.core.RedisElement;
import com.xiaofan0408.message.ServerMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCounted;

import java.util.List;
import java.util.Queue;

public class RedisPacketDecoder extends ByteToMessageDecoder {

  private final Queue<RedisElement> responseReceivers;
  private final AbstractConnection connection;

  private RedisElement redisElement;
  private int stateCounter = 0;

  public RedisPacketDecoder(Queue<RedisElement> responseReceivers, AbstractConnection connection) {
    this.responseReceivers = responseReceivers;
    this.connection = connection;
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
    while (buf.readableBytes() > 4) {
      int length = buf.readableBytes();

      // packet not complete
      if (buf.readableBytes() < 0) return;

      // create Object from packet
      ByteBuf packet = buf.readBytes(length);
      handleBuffer(packet);
      packet.release();
    }
  }

  private void handleBuffer(ByteBuf packet) {
    if (redisElement == null && !loadNextResponse()) {
      throw new RedisException(
          "unexpected message received when no command was send");
    }
    ServerMessage msg = null;
    try {
      msg = RedisCodec.decode(packet);
      redisElement.getSink().next(msg);
      if (msg.ending()) {
        if (redisElement != null) {
          // complete executed only after setting next element.
          RedisElement element = redisElement;
          loadNextResponse();
          element.getSink().complete();
        }
        connection.sendNext();
      }
    } finally {
      if (msg instanceof ReferenceCounted) {
        ((ReferenceCounted) msg).release();
      }
    }
  }

  public void connectionError(Throwable err) {
    if (redisElement != null) {
      redisElement.getSink().error(err);
      redisElement = null;
    }
  }

  public AbstractConnection getClient() {
    return connection;
  }


  public int getStateCounter() {
    return stateCounter;
  }

  public void setStateCounter(int counter) {
    stateCounter = counter;
  }

  public void decrementStateCounter() {
    stateCounter--;
  }


  private boolean loadNextResponse() {
    this.redisElement = responseReceivers.poll();
    if (redisElement != null) {
      return true;
    }
    return false;
  }

}
