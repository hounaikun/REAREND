package com.kun;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-21 11:25
 **/

public class AttemptLocking {
    private Lock lock = new ReentrantLock();
    public void untimed(){
        /*
        * tryLock()方法是有返回值的，它表示用来尝试获取锁，如果获取成功，则返回true，
        * 如果获取失败（即锁已被其他线程获取），则返回false，这个方法无论如何都会立即返回。
        * 在拿不到锁时不会一直在那等待。
         * */
        boolean captured = lock.tryLock();
        try {
            System.out.println("tryLock()："+captured);
        } finally {
            if(captured){
                lock.unlock();
            }
        }
    }
    public void timed(){
        boolean captured = false;
        try {
            /*
            * tryLock()有一个重载方法，这个方法就是：**tryLock(long time , TimeUnit unit)**方法，
            * 这个方法去限定了一个尝试获取锁的时间。
                —获取锁成功则返回true；
                —当失败是分为两种情况：
                    在参数范围内，则不会立即返回值，会等待一段时间，这个时间就是传入的具体参数值，在这个时间内获取锁成功，
                        则依旧返回true；
                    当过了参数范围后，还是获取锁失败，则立即返回false。
            * */
            captured = lock.tryLock(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            System.out.println("tryLock(2, TimeUnit.SECONDS)："+captured);
        } finally {
            if(captured){
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        final AttemptLocking attemptLocking = new AttemptLocking();
        attemptLocking.untimed();
        attemptLocking.timed();
        new Thread(){
            {
                setDaemon(true);
            }
            @Override
            public void run() {
                attemptLocking.lock.lock();
                System.out.println("acquired");
            }
        }.start();
        Thread.yield();
        attemptLocking.untimed();
        attemptLocking.timed();
    }
}
