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


    public T acquire()  {
        try {
            semaphore.acquire();
            if (objects.isEmpty()) {
                return generator.get();
            }
            T data = objects.pop();
            return data;
        }catch (Exception e){}
        return null;
    }

    public void release(T data) {
        if (data == null) {
            return;
        }
        objects.push(data);
        semaphore.release();
    }
}
