package com.xiaofan0408.codec;


import com.xiaofan0408.message.RedisServerMessage;
import com.xiaofan0408.message.ServerMessage;
import com.xiaofan0408.model.RedisObject;
import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Character.isDigit;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.stream.Collectors.joining;

/**
 * @author xuzefan  2020/9/4 14:25
 */
public class RedisCodec {

    private static AsyncRedisParser parser = new AsyncRedisParser();

    public static byte[] translateCommand(String command) {
        String[] args = command.split(" +");

        String finalCommand = "*" + args.length + "\r\n" + Arrays.stream(args)
                .map(s -> "$" + s.length() + "\r\n" + s + "\r\n")
                .collect(joining());
        return finalCommand.getBytes(US_ASCII);
    }

    public static ServerMessage decode(ByteBuf byteBuf){
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        List<RedisObject> redisObjects = parser.feed(bytes);
        return new RedisServerMessage(redisObjects.get(0));
    }
}
