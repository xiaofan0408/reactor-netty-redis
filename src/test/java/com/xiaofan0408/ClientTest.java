package com.xiaofan0408;

import com.xiaofan0408.common.command.StringCommand;
import com.xiaofan0408.impl2.RedisConnectionExTwo;
import org.junit.Before;
import org.junit.Test;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
                .setPoolSize(20)
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

    @Test
    public void testRandKey() {
        String key = redisClient.randomKey().blockFirst();
        System.out.println(key);
    }


    @Test
    public void testMore(){

        long startTime = System.currentTimeMillis();
        for (int i=0; i<10000;i++) {
            redisClient.randomKey().blockFirst();
        }
        System.out.println("cost: " + (System.currentTimeMillis() - startTime));
    }

    @Test
    public void testMulti2() throws InterruptedException {
        StringCommand stringCommand = redisClient.getStringCommandSync();
        Stream.of(1,2,3,4,5).parallel().forEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                stringCommand.set("hello" + integer.intValue(),"world" +  + integer.intValue()).subscribe(System.out::println);
                stringCommand.get("hello" + integer.intValue()).subscribe(serverMessage -> {
                    System.out.println(integer +":" +serverMessage.getData().toString());
                });
            }
        });
        TimeUnit.MILLISECONDS.sleep(5000);
    }

}
