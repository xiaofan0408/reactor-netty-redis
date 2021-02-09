package com.xiaofan0408.impl1.model;

public interface RedisObject {
    enum Type {
        BULK_STRING,
        ERROR,
        ARRAY,
        INTEGER,
        SIMPLE_STRING,
    }

    Type getType();
}
