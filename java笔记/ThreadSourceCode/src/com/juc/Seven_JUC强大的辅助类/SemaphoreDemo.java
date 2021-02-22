package com.juc.Seven_JUC强大的辅助类;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @description: <p>在信号量上我们定义两种操作：
 * acquire（获取） 当一个线程调用acquire操作时，它要么通过成功获取信号量（信号量减1），
 * 要么一直等下去，直到有线程释放信号量，或超时。
 * release（释放）实际上会将信号量的值加1，然后唤醒等待的线程。
 * 信号量主要用于两个目的，一个是用于多个共享资源的互斥使用，另一个用于并发线程数的控制。</p>
 * 类似：抢车位
 * @author: hounaikun
 * @create: 2021-01-15 00:13
 **/
public class SemaphoreDemo {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3); //3个车位
        for (int i = 0; i < 6; i++) { //6个人来抢
            new Thread(() -> {
                try {
                    semaphore.acquire(); //抢到了，就往下执行，抢不到，就阻塞
                    TimeUnit.SECONDS.sleep(5); // 使用5 秒钟
                    System.out.println(Thread.currentThread().getName() + "停了5s");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release(); //使用完就释放车位，并唤醒阻塞的线程
                }
            },"car" + (i + 1)).start();
        }
    }
}
