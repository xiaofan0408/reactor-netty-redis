package com.xiaofan0408;

import com.xiaofan0408.common.command.StringCommand;
import com.xiaofan0408.impl2.RedisConnectionExTwo;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author zefan.xzf
 * @date 2021/12/6 15:47
 */
public class ClientTest {


    private RedisClient redisClient;

    @Before
    public void init() {
        redisClient = RedisClient.builder()
                .setHost("127.0.0.1")
                .setPort(6779)
                .setConnection(RedisConnectionExTwo.class)
                .build();
    }

    @Test
    public void testCreate() {
        String pong = redisClient.ping().blockFirst();
        System.out.println(pong);

    }

    @Test
    public void testExist() {
        Boolean exists =  redisClient.exists("hello").blockFirst();
        System.out.println(exists);
        Boolean exists2 =  redisClient.exists("hello2").blockFirst();
        System.out.println(exists2);
    }

    @Test
    public void testStringCommand() throws InterruptedException {
        StringCommand stringCommand = redisClient.getStringCommandSync();
        stringCommand.set("hello","world").subscribe(System.out::println);
        stringCommand.get("hello").subscribe(System.out::println);
        stringCommand.get("hello").subscribe(System.out::println);
        TimeUnit.MILLISECONDS.sleep(200);
    }

    @Test
    public void testKeys() {
        Set<String> set =  redisClient.keys("rec_bou*").blockFirst();
        System.out.println(set);
    }

}
