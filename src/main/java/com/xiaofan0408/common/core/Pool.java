package com.xiaofan0408.common.core;

import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

/**
 * @author zefan.xzf
 * @date 2021/12/7 18:14
 */
public class Pool<T> {

    private Semaphore semaphore;

    private LinkedList<T> objects;

    private Supplier<T> generator;


    public Pool(int n, Supplier<T> generator) {
        semaphore = new Semaphore(n);
        objects = new LinkedList<>();
        this.generator = generator;
    }


    public T acquire() throws InterruptedException {
        semaphore.acquire();
        T data = objects.pop();
        if (Objects.isNull(data)) {
            return generator.get();
        }
        return data;
    }

    public void release(T data) {
        objects.push(data);
        semaphore.release();
    }
}
