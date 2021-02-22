package com.juc.Seven_JUC强大的辅助类;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @description: <p>让一组线程到达一个屏障（也可以叫同步点）时被阻塞，
 * 直到最后一个线程到达屏障时，屏障才会开门，所有被屏障拦截的线程才会继续干活。
 * 线程进入屏障通过CyclicBarrier的await()方法。
 * 集齐7颗龙珠就可以召唤神龙 </p>
 * @author: hounaikun
 * @create: 2021-01-14 23:43
 **/
public class CyclicBarrierDemo {
    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        // 只有7个线程到达屏障后 ， 这个任务才会执行
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7,() -> {
            System.out.println("七个龙珠集齐成功，神龙，出现吧！");
        });
        for (int i = 0; i < 7; i++) {
            new Thread(() -> {
                try {
                    System.out.println("找到了" + Thread.currentThread().getName());
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            },i+1+"星珠").start();
        }
    }
}
