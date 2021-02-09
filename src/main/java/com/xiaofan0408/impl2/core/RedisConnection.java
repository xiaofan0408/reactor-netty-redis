package com.xiaofan0408.impl2.core;

import com.xiaofan0408.common.core.AbstractConnection;
import com.xiaofan0408.impl1.codec.RedisCodec;
import com.xiaofan0408.common.command.StringCommand;
import com.xiaofan0408.common.enu.RedisException;
import com.xiaofan0408.common.message.ClientMessage;
import com.xiaofan0408.common.message.ServerMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import reactor.core.Disposable;
import reactor.core.publisher.*;
import reactor.netty.Connection;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class RedisConnection implements AbstractConnection {

    private static final Logger logger = Loggers.getLogger(RedisConnection.class);

    private Connection connection;

    private final EmitterProcessor<ClientMessage> requestProcessor = EmitterProcessor.create(false);

    private final FluxSink<ClientMessage> fluxSink = requestProcessor.sink();

    private final EmitterProcessor<ServerMessage> responseProcessor = EmitterProcessor.create(false);

    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private final RequestQueue requestQueue = new RequestQueue();


    private ByteBufAllocator alloc;

    public RedisConnection(Connection connection) {
        this.connection = connection;
        this.alloc = this.connection.outbound().alloc();
        if (logger.isTraceEnabled()) {
            connection.addHandlerFirst(
                    LoggingHandler.class.getSimpleName(),
                    new LoggingHandler(com.xiaofan0408.impl1.core.RedisConnection.class, LogLevel.TRACE));
        }
        connection
                .inbound()
                .receive()
                .doOnError(this::handleConnectionError)
                .subscribe(this::handleIncomingFrames);

        this.requestProcessor
                .flatMap(message -> {
                    return connection.outbound().send(Flux.just(message.encode(alloc)));
                })
                .subscribe();
    }

    @Override
    public Mono<Void> close() {
        return Mono.defer(
            () -> {
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
    }


    @Override
    public String toString() {
        return "Client{isClosed=" + isClosed + '}';
    }

    @Override
    public Flux<ServerMessage> sendPacket(ClientMessage request) {
        requireNonNull(request, "request must not be null");
        return Mono.<Flux<ServerMessage>>create(sink -> {
            if (!isConnected()) {
                if (request instanceof Disposable) {
                    ((Disposable) request).dispose();
                }
                sink.error(new RedisException("connection closed"));
                return;
            }
            Flux<ServerMessage> responses = responseProcessor
                    .doOnSubscribe(ignored -> fluxSink.next(request))
                    .handle(new BiConsumer() {
                        @Override
                        public void accept(Object o, Object o2) {
                            SynchronousSink synchronousSink = (SynchronousSink)o2;
                            synchronousSink.next(o);
                            synchronousSink.complete();
                        }
                    });
            requestQueue.submit(RequestTask.wrap(request, sink, responses));
        }).flatMapMany(Function.identity()).doAfterTerminate(requestQueue);
    }

    @Override
    public boolean isConnected() {
        if (this.isClosed.get()) {
            return false;
        }
        return this.connection.channel().isOpen();
    }

    @Override
    public void sendNext() {

    }

    private void handleIncomingFrames(ByteBuf frame) {
        this.responseProcessor.onNext(RedisCodec.decode(frame));
    }

    public StringCommand getStringCommand(){
        return new StringCommand(this);
    }
}
