package com.juc.four_线程间通信;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description: <p>lock锁</p>
 * @author: hounaikun
 * @create: 2021-01-10 22:56
 **/
public class Lockk {
    private int count = 0;
    private Lock lock = new ReentrantLock(); // 一个锁
    private Condition condition1 = lock.newCondition(); // 一个阻塞队列

    public void increment() {
        lock.lock();
        try {
            while(count != 0) {
                condition1.await(); //拿到锁的的线程阻塞，进入到condition1的阻塞队列
            }
            count ++;
            System.out.println(Thread.currentThread().getName() + ": " + count);
            condition1.signalAll(); // condition1 队列中的所有线程被唤醒
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void decrement() {
        lock.lock();
        try {
            while(count == 0) {
                condition1.await();
            }
            count --;
            System.out.println(Thread.currentThread().getName() + ": " + count);
            condition1.signalAll(); // condition1 队列中的所有线程被唤醒
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        Lockk lockk = new Lockk();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                lockk.increment();
            }
        },"A").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                lockk.decrement();
            }
        },"B").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                lockk.increment();
            }
        },"A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                lockk.decrement();
            }
        },"B").start();
    }
}
