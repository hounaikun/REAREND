package com.kun;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-14 11:00
 **/
//1.自定义类继承于Thread类
class MyThread extends Thread {

    private int cyclicBaseNum;
    public MyThread(int cyclicBaseNum) {
        this.cyclicBaseNum = cyclicBaseNum;
    }

    //2.重写run方法
    @Override
    public void run() {
        int i = this.cyclicBaseNum;
        while (i-- > 0) {
            System.out.println(i + "--MyThread线程输出");
        }
    }


}
public class ThreadTest{

    public static void main(String[] args) {
        //3.创建自定义类的对象
        MyThread myThread = new MyThread(10);
        //4.启动子线程
        //start的作用：1.导致此线程开始执行; 2.Java虚拟机调用此线程的run方法。
        //多次启动同一线程（对象）是不合法的。 如果需要重新启动线程，需要重新new对象。否则抛IllegalThreadStateException
        myThread.start();
        //myThread.start(); //Exception in thread "main" java.lang.IllegalThreadStateException

        int i = 10;
        while (i-- > 0) {
            System.out.println(i + "--main线程输出");
        }
    }
}