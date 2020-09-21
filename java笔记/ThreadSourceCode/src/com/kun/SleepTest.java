package com.kun;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-16 14:36
 **/
class MySleep implements Runnable {
    private int countDown = 10;
    private static int taskCount = 0;
    private final int id = taskCount++;

    public MySleep() {
    }

    public MySleep(int countDown) {
        this.countDown = countDown;
    }

    public String status() {
        return "#" + id + "(" +
                (countDown > 0 ? countDown : "LiftOff!") + "),";
    }

    //2.重写run方法
    @Override
    public void run() {
        while (countDown-- > 0) {
            System.out.print(status());
            try {
                //Old-Style
                Thread.sleep(100);
                //Java SE5Up-Style
//                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class SleepTest {
    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 3; i++) {
            exec.execute(new MySleep(5));
        }
        exec.shutdown();
    }
}
