package site.shanzhao.soil.basis.thread.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author tanruidong
 * @date 2020/11/21 16:09
 */
public class BoundedQueue<T> {

    private Object[] object;
    private int count; // 队列中实际数量
    private int addIndex; // 下一次添加的位置索引
    private int rmIndex;  // 下一次删除的位置索引
    private int capacity;
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition(); // 非空，表示可取出
    private final Condition notFull = lock.newCondition(); // 非满，表示可添加

    BoundedQueue(int capacity){
        if (capacity <= 0) throw new IllegalArgumentException("容量必须为正数");
        object = new Object[capacity];
        this.capacity = capacity;
    }
    public void add(T t) throws InterruptedException {
        lock.lock();
        try{
            while (count == capacity) // 注意这里是while，不是if
                notFull.await();
            object[addIndex] = t;
            addIndex++;
            if (addIndex == object.length){
                addIndex = 0;
            }
            count++;
            notEmpty.signal();
        }finally {
            lock.unlock();
        }
    }

    public T get() throws InterruptedException {
        lock.lock();
        try{
            while (count == 0)
                notEmpty.await();
            @SuppressWarnings("unchecked")
            T o = (T)object[rmIndex];
            rmIndex++;
            if (rmIndex == object.length){
                rmIndex = 0;
            }
            count--;
            notFull.await();
            return o;
        }finally {
            lock.unlock();
        }
    }
}
