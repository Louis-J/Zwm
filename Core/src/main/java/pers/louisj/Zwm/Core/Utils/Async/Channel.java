package pers.louisj.Zwm.Core.Utils.Async;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Channel<T> {
    protected static final int initCapa = 128;
    public static final AtomicInteger writeWaitTimes = new AtomicInteger(0);

    protected T[] items;
    protected ReentrantLock lock = new ReentrantLock(true); // better performance in fair lock than unfair lock
    protected Condition condition = lock.newCondition();

    int headIndex;
    int size;

    @SuppressWarnings("unchecked")
    public Channel() {
        items = (T[]) new Object[initCapa];
    }

    @SuppressWarnings("unchecked")
    public Channel(int size) {
        items = (T[]) new Object[size];
    }

    public T take() {
        T message = null;
        lock.lock();
        try {
            while (size == 0)
                condition.await();
            message = items[headIndex];
            if (++headIndex == items.length)
                headIndex = 0;
            size--;
            condition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return message;
    }

    public void put(T message) {
        // lock.lock();
        if (!lock.tryLock()) {
            writeWaitTimes.incrementAndGet();
            lock.lock();
        }
        try {
            // expand
            if (size == items.length)
                resize(items.length * 2);

            items[realIndex(size)] = message;
            size++;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapa) {
        T[] newQue = (T[]) new Object[newCapa];
        for (int i = 0; i < size; i++) {
            newQue[i] = items[realIndex(i)];
        }
        items = newQue;
        headIndex = 0;
    }

    private int realIndex(int index) {
        return (headIndex + index) % items.length;
    }

    public int GetCapacity() {
        return items.length;
    }
}