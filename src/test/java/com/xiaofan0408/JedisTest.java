package com.xiaofan0408;

import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * @author zefan.xzf
 * @date 2021/12/6 17:43
 */
public class JedisTest {

    private Jedis jedis;

    @Before
    public void init() {
        jedis = new Jedis("127.0.0.1",6779);
    }

    @Test
    public void testMore() {
        long startTime = System.currentTimeMillis();
        for (int i=0; i<10000;i++) {
            jedis.randomKey();
        }
        System.out.println("cost: " + (System.currentTimeMillis() - startTime));
    }

}
