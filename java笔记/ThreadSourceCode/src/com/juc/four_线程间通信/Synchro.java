package com.juc.four_线程间通信;

/**
 * @description: <p>synchronized实现</p>
 * @author: hounaikun
 * @create: 2021-01-10 21:23
 **/
public class Synchro {
    private int count = 0;

    public synchronized void increment() throws InterruptedException {
        while(count != 0){
            this.wait(); // 拿到锁的线程阻塞，进入到阻塞队列，这种方式只用一个阻塞队列
        }
        count++;
        System.out.println(Thread.currentThread().getName() + ": " + count);
        this.notifyAll(); // 队列中的所有线程被唤醒
    }

    public synchronized void decrement() throws InterruptedException {
        while(count == 0) {
            this.wait();
        }
        count--;
        System.out.println(Thread.currentThread().getName() + ": " + count);
        this.notifyAll();
    }

    public static synchronized void increment1() throws InterruptedException {
        Synchro.class.wait(); //静态方法阻塞
    }

    /*
        一个线程 输出0，一个线程 输出1；循环输出；
     */
    public static void main(String[] args) {
        Synchro synchro = new Synchro();
//        try {
//            Synchro.increment1();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    synchro.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    synchro.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    synchro.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"B").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    synchro.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"B").start();
    }
}
