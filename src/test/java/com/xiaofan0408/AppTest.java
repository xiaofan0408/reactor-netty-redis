package com.xiaofan0408;



import com.xiaofan0408.command.StringCommand;
import com.xiaofan0408.core.RedisClient;
import com.xiaofan0408.core.RedisConnection;
import com.xiaofan0408.message.impl.PingPacket;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;


/**
 * Unit test for simple App.
 */
@Slf4j
public class AppTest 
{
    private RedisClient redisClient;

    @Before
    public void init(){
        redisClient = new RedisClient("118.25.217.97",6777);
    }


    @Test
    public void testSendPingPacket() throws InterruptedException {
        RedisConnection connect = redisClient.connect();
        connect.sendPacket(new PingPacket()).subscribe(System.out::println);
        TimeUnit.MILLISECONDS.sleep(100);
    }

    @Test
    public void testStringCommand() throws InterruptedException {
        RedisConnection connect = redisClient.connect();
        StringCommand stringCommand = connect.getStringCommand();
        stringCommand.ping().subscribe(System.out::println);
        stringCommand.set("hello","world").subscribe(System.out::println);
        stringCommand.get("hello").subscribe(System.out::println);
        stringCommand.get("hello").subscribe(System.out::println);
        TimeUnit.MILLISECONDS.sleep(200);
    }

    @Test
    public void testMulti() throws InterruptedException {
        RedisConnection connect = redisClient.connect();
        StringCommand stringCommand = connect.getStringCommand();
        Stream.of(1,2,3,4,5).parallel().forEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                stringCommand.set("hello" + integer.intValue(),"world" +  + integer.intValue()).subscribe(System.out::println);
                stringCommand.get("hello" + integer.intValue()).subscribe(System.out::println);
            }
        });
        TimeUnit.MILLISECONDS.sleep(1000);
    }

}
