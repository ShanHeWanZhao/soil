package com.github.soil.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author tanruidong
 * @since 2024/05/17 00:02
 */
public class ThreadSafeList<T> {
    private volatile Object[] dataArr;

    private int size;

    private final int maxLength;
    private final Lock readLock;
    private final Lock writeLock;

    public ThreadSafeList(int initLength, int maxLength) {
        this.dataArr = new Object[initLength];
        this.maxLength = maxLength;
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        readLock = readWriteLock.readLock();
        writeLock = readWriteLock.readLock();
    }

    public void add(T target){
        if (dataArr.length == size && size < maxLength){
            // todo 扩容
        }
        try {
            writeLock.lock();
            dataArr[size] = target;
            size++;
        }finally {
            writeLock.unlock();
        }
    }

    public T get(){
        return get(0);
    }
    public T get(int index){
        try {
            readLock.tryLock();
            Object result = dataArr[index];
            return (T)result;
        }finally {
            readLock.unlock();
        }
    }

    public T delete(int index){
        try {
            writeLock.lock();
            Object result = dataArr[index];
            while(index < size){
                Object o = dataArr[index + 1];
                dataArr[index] = o;
                index++;
            }
            size--;
            return (T)result;
        }finally {
            writeLock.unlock();
        }
    }
}
