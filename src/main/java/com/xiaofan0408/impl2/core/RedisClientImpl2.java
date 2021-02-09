package com.xiaofan0408.impl2.core;

import reactor.netty.tcp.TcpClient;

public class RedisClientImpl2 {
    private String host;

    private Integer port;

    private TcpClient tcpClient;

    public RedisClientImpl2(String host, Integer port) {
        this.host = host;
        this.port = port;
        tcpClient = TcpClient.create().host(host).port(port);
    }

    public RedisConnection connect(){
        return new RedisConnection(tcpClient.connectNow());
    }
}
