package com.juc.Seven_JUC强大的辅助类;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

/**
 * @description:
 *   <p> CountDownLatch主要有两个方法，当一个或多个线程调用await方法时，这些线程会阻塞。
 *       其它线程调用countDown方法会将计数器减1(调用countDown方法的线程不会阻塞)，
 *       计数器的值变为0时，因await方法阻塞的线程会被唤醒，继续执行。
 *       * 解释：* 6个同学陆续离开教室后值班同学才可以关门。
 *   </p>
 * @author: hounaikun
 * @create: 2021-01-14 23:22
 **/
public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(6);
        for (int i = 0; i < 6; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " 同学走了");
                countDownLatch.countDown();
            },"A"+i).start();
        }
        countDownLatch.await();;
        System.out.println("main 同学关门了");
    }
}
