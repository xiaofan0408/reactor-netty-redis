package com.xiaofan0408.common.core;

import com.xiaofan0408.common.message.ClientMessage;
import com.xiaofan0408.common.message.ServerMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AbstractConnection {

  Mono<Void> close();

  Flux<ServerMessage> sendPacket(ClientMessage requests);

  boolean isConnected();

  void sendNext();
}
