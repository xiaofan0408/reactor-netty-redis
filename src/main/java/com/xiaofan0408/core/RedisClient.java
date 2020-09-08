package com.xiaofan0408.core;

import reactor.netty.tcp.TcpClient;

public class RedisClient {

    private String host;

    private Integer port;

    private TcpClient tcpClient;

    public RedisClient(String host, Integer port) {
        this.host = host;
        this.port = port;
        tcpClient = TcpClient.create().host(host).port(port);
    }

    public RedisConnection connect(){
        return new RedisConnection(tcpClient.connectNow());
    }

    public RedisConnection2 connect2(){
        return new RedisConnection2(tcpClient.connectNow());
    }

}
