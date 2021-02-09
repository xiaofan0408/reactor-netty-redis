package com.xiaofan0408;



import com.xiaofan0408.common.command.StringCommand;
import com.xiaofan0408.impl1.core.RedisClientImpl1;
import com.xiaofan0408.impl2.core.RedisClientImpl2;
import com.xiaofan0408.impl2.core.RedisConnection;
import com.xiaofan0408.common.message.impl.PingPacket;
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
    private RedisClientImpl1 redisClientImpl1;

    private RedisClientImpl2 redisClientImpl2;

    @Before
    public void init(){
        redisClientImpl1 = new RedisClientImpl1("118.25.217.97",6777);

        redisClientImpl2 = new RedisClientImpl2("118.25.217.97",6777);
    }


    @Test
    public void testSendPingPacket() throws InterruptedException {
        com.xiaofan0408.impl1.core.RedisConnection connect = redisClientImpl1.connect();
        connect.sendPacket(new PingPacket()).subscribe(System.out::println);
        TimeUnit.MILLISECONDS.sleep(100);
    }

    @Test
    public void testStringCommand() throws InterruptedException {
        com.xiaofan0408.impl1.core.RedisConnection connect = redisClientImpl1.connect();
        StringCommand stringCommand = connect.getStringCommand();
        stringCommand.ping().subscribe(System.out::println);
        stringCommand.set("hello","world").subscribe(System.out::println);
        stringCommand.get("hello").subscribe(System.out::println);
        stringCommand.get("hello").subscribe(System.out::println);
        TimeUnit.MILLISECONDS.sleep(200);
    }

    @Test
    public void testMulti() throws InterruptedException {
        com.xiaofan0408.impl1.core.RedisConnection connect = redisClientImpl1.connect();
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

    @Test
    public void testConnection2() throws InterruptedException {
        RedisConnection connect = redisClientImpl2.connect();
        StringCommand stringCommand = connect.getStringCommand();
        stringCommand.ping().subscribe(System.out::println);
        stringCommand.set("hello","world").subscribe(System.out::println);
        stringCommand.get("hello").subscribe(System.out::println);
        stringCommand.get("hello").subscribe(System.out::println);
        TimeUnit.MILLISECONDS.sleep(200);
    }

    @Test
    public void testMulti2() throws InterruptedException {
        RedisConnection connect = redisClientImpl2.connect();
        StringCommand stringCommand = connect.getStringCommand();
        Stream.of(1,2,3,4,5).parallel().forEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                stringCommand.set("hello" + integer.intValue(),"world" +  + integer.intValue()).subscribe(System.out::println);
                stringCommand.get("hello" + integer.intValue()).subscribe(System.out::println);
            }
        });
        TimeUnit.MILLISECONDS.sleep(2000);
    }

}
