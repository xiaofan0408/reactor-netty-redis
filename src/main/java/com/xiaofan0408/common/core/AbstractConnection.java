package com.xiaofan0408.common.core;

import com.xiaofan0408.common.message.ClientMessage;
import com.xiaofan0408.common.message.ServerMessage;
import com.xiaofan0408.common.model.RedisError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AbstractConnection {

  Mono<Void> close();

  default Flux<ServerMessage> sendPacket(ClientMessage requests){
    return doSend(requests).concatMap(serverMessage -> {
      if (serverMessage.getData() instanceof RedisError) {
        return Flux.error(new Exception(serverMessage.getData().toString()));
      }
      return Flux.just(serverMessage);
    });
  }

  Flux<ServerMessage> doSend(ClientMessage requests);

  boolean isConnected();

  void sendNext();
}
