package pers.louisj.Zwm.Core.Utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Channel2<T> extends Channel<T> {
    private static final int initCapa = 128;

    private T[] items;
    private ReentrantLock lock = new ReentrantLock(false);
    private Condition condition = lock.newCondition();

    int headIndex;
    int size;

    @SuppressWarnings("unchecked")
    public Channel2() {
        items = (T[]) new Object[initCapa];
    }

    @SuppressWarnings("unchecked")
    public Channel2(int size) {
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
        lock.lock();
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
}