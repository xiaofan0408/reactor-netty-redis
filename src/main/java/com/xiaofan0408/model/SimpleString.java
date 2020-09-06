package com.xiaofan0408.model;


public class SimpleString implements RedisObject {

    private final String value;

    public SimpleString(String value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.SIMPLE_STRING;
    }

    @Override
    public String toString() {
        return value;
    }
}
