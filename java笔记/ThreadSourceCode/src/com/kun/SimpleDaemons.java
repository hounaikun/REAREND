package com.kun;

import java.util.concurrent.TimeUnit;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-16 16:51
 **/
public class SimpleDaemons implements Runnable {
    @Override
    public void run() {
        while(true){
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                System.out.println(Thread.currentThread() + " " + this);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            Thread daemon = new Thread(new SimpleDaemons());
            daemon.setDaemon(true); //必须声明在start()之前
            daemon.start();
        }
        System.out.println("All daemons started");
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
