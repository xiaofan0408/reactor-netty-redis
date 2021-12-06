package com.xiaofan0408.impl1;

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

    public RedisConnectionExOne connect(){
        return new RedisConnectionExOne(tcpClient.connectNow());
    }

}
