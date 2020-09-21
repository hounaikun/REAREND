package com.kun;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-16 14:58
 **/
class MySleepRunnable implements Runnable {
    private final int ID;

    public MySleepRunnable(int id) {
        this.ID = id;
    }

    @Override
    public void run() {
        Random random = new Random();
        int seconds = random.nextInt(10) + 1;
        try {
            TimeUnit.SECONDS.sleep(seconds);
            System.out.println("Thread[" + this.ID + "] -- I have been sleeping for " + seconds + " seconds");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class SleepPractise {
    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            exec.execute(new MySleepRunnable(i));
        }
        exec.shutdown();
    }
}
