package com.xiaofan0408.common.message;


import com.xiaofan0408.common.model.RedisObject;

/**
 * @author xuzefan  2020/9/4 15:25
 */
public class RedisServerMessage implements ServerMessage<RedisObject>{

    private RedisObject redisObject;

    public RedisServerMessage(RedisObject redisObject) {
        this.redisObject = redisObject;
    }

    @Override
    public RedisObject getData() {
        return redisObject;
    }

    @Override
    public boolean ending() {
        return true;
    }

    @Override
    public String toString() {
        return redisObject.toString();
    }
}
