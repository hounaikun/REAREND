package com.juc.Five_线程间定制化通用通信;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2021-01-10 23:52
 **/
public class ThreadOrderAccess {
    private int flag = 0; // 0 A, 1 B, 3 C

    private Lock lock = new ReentrantLock();
    private Condition condition1 = lock.newCondition();

    public void printA() {
        lock.lock();
        try {
            while(flag != 0) {
                condition1.await();
            }
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName()+": AA");
            }
            flag = 1;
            condition1.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printB() {
        lock.lock();
        try {
            while(flag != 1) {
                condition1.await();
            }
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName()+": BB");
            }
            flag = 2;
            condition1.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printC() {
        lock.lock();
        try {
            while(flag != 2) {
                condition1.await();
            }
            for (int i = 0; i < 15; i++) {
                System.out.println(Thread.currentThread().getName()+": CC");
            }
            flag = 0;
            condition1.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ThreadOrderAccess threadOrderAccess = new ThreadOrderAccess();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                threadOrderAccess.printA();
            }
        },"线程1").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                threadOrderAccess.printB();
            }
        },"线程2").start();
        new Thread(() -> {
            for (int i = 0; i < 15; i++) {
                threadOrderAccess.printC();
            }
        },"线程3").start();
    }
}
