package com.xiaofan0408.enu;

/**
 * @author xuzefan  2020/9/4 14:19
 */
public class RedisException extends RuntimeException {

    private String code;

    public RedisException(String message) {
        super(message);
    }

    public RedisException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisException(String message, String code,Throwable cause) {
        super(message, cause);
    }


}
