package com.kun;

import java.util.concurrent.TimeUnit;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-14 14:43
 **/
//1.自定义类实现Runnable接口
class LiftOff implements Runnable {
    private int countDown = 10;
    private static int taskCount = 0;
    private final int id = taskCount++;

    public LiftOff() {
    }
    public LiftOff(int countDown) {
        this.countDown = countDown;
    }

    public String status(){
        return "#" + id + "(" +
                (countDown  > 0 ? countDown : "LiftOff!") + "),";
    }
    //2.重写run方法
    @Override
    public void run() {
        while(countDown -- > 0){
            System.out.print(status());
            //对线程调度器（可以将CPU从一个线程转移到另一个线程）的一种建议，
            Thread.yield();
        }
    }
}

public class RunnableTest{
    public static void main(String[] args) {
        //3.Runnable的子类本身不具有线程能力，必须将其实例化对象显示的附到线程(Thread)上
        Thread thread = new Thread(new LiftOff());
        //        //4.启动子线程
        thread.start();

        System.out.println("Waiting for LiftOff");
    }
}
