package com.kun;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-21 10:36
 **/
class MyRunnable03 implements Runnable {
    private SynchronizedTest synchronizedTest;

    public MyRunnable03(SynchronizedTest synchronizedTest) {
        this.synchronizedTest = synchronizedTest;
    }

    @Override
    public void run() {
        synchronizedTest.update();
    }

    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        SynchronizedTest synchronizedTest = new SynchronizedTest();
        for (int i = 0; i < 5; i++) {
            exec.execute(new MyRunnable03(synchronizedTest));
        }
        exec.shutdown();
    }
}

public class SynchronizedTest {
    private int count;
    private String content;

    public synchronized void update() {
        count++;
        content = "你被" + Thread.currentThread().getName() + " 访问，此时count = " + count;
        System.out.println(content);
    }
}

