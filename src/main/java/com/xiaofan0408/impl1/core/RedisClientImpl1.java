package com.xiaofan0408.impl1.core;

import com.xiaofan0408.impl2.core.RedisConnection;
import reactor.netty.tcp.TcpClient;

public class RedisClientImpl1 {

    private String host;

    private Integer port;

    private TcpClient tcpClient;

    public RedisClientImpl1(String host, Integer port) {
        this.host = host;
        this.port = port;
        tcpClient = TcpClient.create().host(host).port(port);
    }

    public com.xiaofan0408.impl1.core.RedisConnection connect(){
        return new com.xiaofan0408.impl1.core.RedisConnection(tcpClient.connectNow());
    }

}
