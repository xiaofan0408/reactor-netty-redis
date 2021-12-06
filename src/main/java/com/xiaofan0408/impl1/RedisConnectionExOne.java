package com.xiaofan0408.impl1;


import com.xiaofan0408.common.core.AbstractConnection;
import com.xiaofan0408.common.codec.RedisPacketDecoder;
import com.xiaofan0408.common.codec.RedisPacketEncoder;
import com.xiaofan0408.common.command.StringCommand;
import com.xiaofan0408.common.enu.RedisException;
import com.xiaofan0408.common.message.ClientMessage;
import com.xiaofan0408.common.message.ServerMessage;
import io.netty.channel.Channel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.util.Logger;
import reactor.util.Loggers;
import reactor.util.concurrent.Queues;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class RedisConnectionExOne implements AbstractConnection {

    private static final Logger logger = Loggers.getLogger(RedisConnectionExOne.class);
    protected final ReentrantLock lock = new ReentrantLock();
    protected final Connection connection;
    protected final Queue<RedisElement> responseReceivers = Queues.<RedisElement>unbounded().get();
    protected final Queue<ClientMessage> sendingQueue = Queues.<ClientMessage>unbounded().get();
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private final RedisPacketDecoder redisPacketDecoder;
    private final RedisPacketEncoder redisPacketEncoder = new RedisPacketEncoder();


    public RedisConnectionExOne(Connection connection) {
        this.connection = connection;
        this.redisPacketDecoder = new RedisPacketDecoder(responseReceivers, this);
        connection.addHandler(redisPacketDecoder);
        connection.addHandler(redisPacketEncoder);
        if (logger.isTraceEnabled()) {
            connection.addHandlerFirst(
                    LoggingHandler.class.getSimpleName(),
                    new LoggingHandler(RedisConnectionExOne.class, LogLevel.TRACE));
        }
        connection
                .inbound()
                .receive()
                .doOnError(this::handleConnectionError)
                .then()
                .subscribe();
    }

    private void handleConnectionError(Throwable throwable) {
        RedisException err;
        if (this.isClosed.compareAndSet(false, true)) {
            err =
                    new RedisException("Connection unexpected error", "08000", throwable);
            logger.error("Connection unexpected error", throwable);
        } else {
            err = new RedisException("Connection error", "08000", throwable);
            logger.error("Connection error", throwable);
        }
        clearWaitingListWithError(err);
    }

    @Override
    public Mono<Void> close() {
        return Mono.defer(
            () -> {
                clearWaitingListWithError(
                        new RedisException("Connection is closing"));
                if (this.isClosed.compareAndSet(false, true)) {

                    Channel channel = this.connection.channel();
                    if (!channel.isOpen()) {
                        this.connection.dispose();
                        return this.connection.onDispose();
                    }
                }
                return Mono.empty();
            });
    }

    @Override
    public Flux<ServerMessage> doSend(ClientMessage message) {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        return Flux.create(
            sink -> {
                if (!isConnected()) {
                    sink.error(
                            new RedisException(
                                    "Connection is close. Cannot send anything"));
                    return;
                }
                if (atomicBoolean.compareAndSet(false, true)) {
                    try {
                        lock.lock();
                        if (this.responseReceivers.isEmpty()) {
                            this.responseReceivers.add(new RedisElement(sink, message.getMessage()));
                            connection.channel().writeAndFlush(message);
                        } else {
                            this.responseReceivers.add(new RedisElement(sink, message.getMessage()));
                            sendingQueue.add(message);
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            });
    }

    @Override
    public void sendNext() {
        lock.lock();
        try {
            ClientMessage next = sendingQueue.poll();
            if (next != null) connection.channel().writeAndFlush(next);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isConnected() {
        if (this.isClosed.get()) {
            return false;
        }
        return this.connection.channel().isOpen();
    }

    private void clearWaitingListWithError(Throwable exception) {
        RedisElement response;
        while ((response = this.responseReceivers.poll()) != null) {
            response.getSink().error(exception);
        }
    }

    @Override
    public String toString() {
        return "Client{isClosed=" + isClosed + '}';
    }

    public StringCommand getStringCommand(){
        return new StringCommand(this);
    }
}
