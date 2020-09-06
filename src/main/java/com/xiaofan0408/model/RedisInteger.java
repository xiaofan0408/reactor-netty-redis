package com.xiaofan0408.model;



public class RedisInteger implements RedisObject {

    private final long value;

    public RedisInteger(long value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.INTEGER;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
