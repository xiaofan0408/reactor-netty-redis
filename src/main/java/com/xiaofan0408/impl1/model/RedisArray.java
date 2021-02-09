package com.xiaofan0408.impl1.model;



import java.util.List;


public class RedisArray implements RedisObject {

    private final List<RedisObject> elements;

    public RedisArray(List<RedisObject> elements) {
        this.elements = elements;
    }

    @Override
    public Type getType() {
        return Type.ARRAY;
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}
