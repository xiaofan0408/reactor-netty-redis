package com.xiaofan0408;

import com.xiaofan0408.command.StringCommand;
import com.xiaofan0408.core.RedisClient;
import com.xiaofan0408.core.RedisConnection;
import com.xiaofan0408.message.impl.PingPacket;
import com.xiaofan0408.message.impl.StringPacket;

import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws InterruptedException {
        RedisClient redisClient = new RedisClient("118.25.217.97",6777);
        RedisConnection connect = redisClient.connect();
        connect.sendPacket(new PingPacket()).subscribe(System.out::println);
//        connect.sendCommand(new StringPacket("hello")).subscribe(System.out::println);
//        connect.sendPacket(new StringPacket("hello","world")).subscribe(System.out::println);
//        connect.sendPacket(new StringPacket("hello")).subscribe(System.out::println);
        StringCommand stringCommand = connect.getStringCommand();
        stringCommand.ping().subscribe(System.out::println);
        stringCommand.set("hello","world").subscribe(System.out::println);
        stringCommand.get("hello").subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(10);
    }
}
