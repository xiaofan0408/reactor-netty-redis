package com.xiaofan0408.common.model;




public class RedisError implements RedisObject {

    private final String message;

    public RedisError(String message) {
        this.message = message;
    }

    @Override
    public Type getType() {
        return Type.ERROR;
    }

    @Override
    public String toString() {
        return "RedisError(" + message + ')';
    }
}
