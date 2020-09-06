package com.xiaofan0408.core;

import com.xiaofan0408.command.StringCommand;
import com.xiaofan0408.message.ClientMessage;
import com.xiaofan0408.message.ServerMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AbstractConnection {

  Mono<Void> close();

  Flux<ServerMessage> sendPacket(ClientMessage requests);

  boolean isConnected();

  void sendNext();
}
