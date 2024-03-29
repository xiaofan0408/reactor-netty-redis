package com.xiaofan0408;

import com.xiaofan0408.common.command.StringCommand;
import com.xiaofan0408.common.core.AbstractConnection;
import com.xiaofan0408.common.core.Pool;
import com.xiaofan0408.common.message.impl.PingPacket;
import com.xiaofan0408.common.message.impl.StringPacket;
import com.xiaofan0408.common.model.RedisArray;
import com.xiaofan0408.impl1.RedisConnectionExOne;
import com.xiaofan0408.impl2.RedisConnectionExTwo;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author zefan.xzf
 * @date 2021/12/6 15:28
 */
public class RedisClient {

    private AbstractConnection connection;

    private TcpClient tcpClient;

    private Pool<AbstractConnection> connectionPool;

    private RedisClient() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder{

        private String host;

        private Integer port;

        private Class connectionClass;

        private ConnectionProvider provider;

        private int poolSize = 0;

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(Integer port) {
            this.port = port;
            return this;
        }

        public Builder setConnection(Class connectionClass) {
            this.connectionClass = connectionClass;
            return this;
        }

        public Builder setPoolSize(int size) {
            this.poolSize = size;
            return this;
        }

        public RedisClient build() {

            if (poolSize > 0) {
                this.provider = ConnectionProvider.builder("redis-fixed")
                        .maxConnections(poolSize)
                        .maxIdleTime(Duration.ofSeconds(20))
                        .maxLifeTime(Duration.ofSeconds(60))
                        .pendingAcquireTimeout(Duration.ofSeconds(60))
                        .build();
            }
            RedisClient redisClient  = new RedisClient();
            if (StringUtils.isBlank(host)) {
                host = "127.0.0.1";
            }
            if (Objects.isNull(port)) {
                port = 6379;
            }

            TcpClient tcpClient = TcpClient.create(provider).
                    host(host)
                    .port(port);
            redisClient.tcpClient = tcpClient;
            if (Objects.nonNull(connectionClass)) {
                if (connectionClass.getName().equals(RedisConnectionExOne.class.getName())) {
                    redisClient.connection = new RedisConnectionExOne(tcpClient.connectNow());
                } else {
                    redisClient.connection = new RedisConnectionExTwo(tcpClient.connectNow());
                }
            } else {
                redisClient.connection = new RedisConnectionExTwo(tcpClient.connectNow());
            }


            if (poolSize > 0) {
                redisClient.connectionPool = new Pool<>(poolSize,() -> {
                    AbstractConnection connection;
                    if (Objects.nonNull(connectionClass)) {
                        if (connectionClass.getName().equals(RedisConnectionExOne.class.getName())) {
                            connection = new RedisConnectionExOne(tcpClient.connectNow());
                        } else {
                            connection = new RedisConnectionExTwo(tcpClient.connectNow());
                        }
                    } else {
                        connection = new RedisConnectionExTwo(tcpClient.connectNow());
                    }
                    return connection;
                });
            }

            return redisClient;
        }
    }

    public Mono<Void> close() {
        if (connection.isConnected()) {
            return connection.close();
        }
        return Mono.empty();
    }

    public Flux<String> ping() {
        AbstractConnection localConn = null;
        try {
            if (Objects.nonNull(connectionPool)) {
                localConn = connectionPool.acquire();
            } else {
                localConn = connection;
            }
            return localConn.sendPacket(new PingPacket()).map(serverMessage -> serverMessage.getData().toString());
        } finally {
            if (Objects.nonNull(connectionPool)) {
                connectionPool.release(localConn);
            }
        }
    }

    public Flux<Boolean> exists(String key) {
        return connection.sendPacket(new StringPacket("exists " + key)).map(serverMessage -> {
            return 1L == Long.parseLong(serverMessage.getData().toString());
        });
    }

    public Flux<Long> del(String key) {
        return connection.sendPacket(new StringPacket("del " + key))
                .map(serverMessage -> Long.parseLong(serverMessage.getData().toString()));
    }

    public Flux<String> type(String key) {
        return connection.sendPacket(new StringPacket("type " + key))
                .map(serverMessage -> serverMessage.getData().toString());
    }

    public Flux<Set<String>> keys(String pattern) {
        return connection.sendPacket(new StringPacket("keys " + pattern))
                .map(serverMessage -> {
                    Set<String> set = new HashSet<>();
                    if (serverMessage.getData() instanceof RedisArray) {
                        RedisArray array = (RedisArray)serverMessage.getData();
                        if (Objects.nonNull(array.getElements())) {
                            array.getElements().forEach(redisObject -> {
                                set.add(redisObject.toString());
                            });
                        }
                    } else {
                        set.add(serverMessage.getData().toString());
                    }
                    return set;
                });
    }


    public Flux<String> randomKey() {
        AbstractConnection localConn = null;
        try {
            if (Objects.nonNull(connectionPool)) {
                localConn = connectionPool.acquire();
            } else {
                localConn = connection;
            }
            return connection.sendPacket(new StringPacket("randomKey"))
                    .map(serverMessage -> serverMessage.getData().toString());
        } finally {
            if (Objects.nonNull(connectionPool)) {
                connectionPool.release(localConn);
            }
        }
    }

    public Mono<StringCommand> getStringCommand(){
        return Mono.just(new StringCommand(connection));
    }

    public StringCommand getStringCommandSync() {
        return getStringCommand().block();
    }
}
