package pers.louisj.Zwm.Core.Utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Channel<T> {
    private static final int initCapa = 128;
    public static final AtomicInteger writeWaitTimes = new AtomicInteger(0);

    private T[] items;
    private ReentrantLock lock = new ReentrantLock(false);

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
        // lock.lockInterruptibly();
        lock.lock();
        while (size == 0) {
            try {
                lock.unlock();
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException e) {
            }
            lock.lock();
        }
        T message = items[headIndex];
        if (++headIndex == items.length)
            headIndex = 0;
        size--;
        lock.unlock();
        synchronized (this) {
            notify();
        }
        return message;
    }

    public void put(T message) {
        // lock.lockInterruptibly();
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
        } finally {
            lock.unlock();
        }
        synchronized (this) {
            notify();
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
}