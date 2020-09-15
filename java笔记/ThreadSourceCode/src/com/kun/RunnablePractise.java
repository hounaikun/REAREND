package com.kun;

import java.util.Arrays;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-14 15:51
 **/
class MyRunnable01 implements Runnable {

    public MyRunnable01() {
        System.out.println(Thread.currentThread().getName() + "已被启动！");
    }

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            System.out.println("I am run() of " + Thread.currentThread().getName() + "----" + i);
            Thread.yield();
        }
        System.out.println(Thread.currentThread().getName() + "已被关闭！");
    }
}
class MyRunnable02 implements Runnable {
    private int fibonacciCount;

    public MyRunnable02(int fibonacciCount) {
        this.fibonacciCount = fibonacciCount;
    }

    @Override
    public void run() {
        int[] fib = new int[this.fibonacciCount];
        if(this.fibonacciCount < 1){
            System.out.println("传入数值错误！！！");
        }else if(this.fibonacciCount == 1){
            System.out.println("[0]]");
        }else{
            fib[0] = 0;fib[1] = 1;
            for(int i =2;i<fib.length;i++){
                fib[i] = fib[i-1] + fib[i-2];
            }
            System.out.println(Arrays.toString(fib));
        }
    }
}
public class RunnablePractise {
    public static void main(String[] args) {
//        Thread thread01 = new Thread(new MyRunnable01());
//        thread01.start();
//        Thread thread02 = new Thread(new MyRunnable01());
//        thread02.start();
//        Thread thread03 = new Thread(new MyRunnable01());
//        thread03.start();

        Thread thread04 = new Thread(new MyRunnable02(8));
        thread04.start();
        Thread thread05 = new Thread(new MyRunnable02(9));
        thread05.start();
        Thread thread06 = new Thread(new MyRunnable02(10));
        thread06.start();
    }
}
