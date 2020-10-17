package com.kun;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-18 16:45
 **/
class ExceptionThread2 implements Runnable{
    @Override
    public void run() {
        Thread t = Thread.currentThread();
        System.out.println(t+"===="+t.getUncaughtExceptionHandler());
        throw new RuntimeException(); //抛出一个运行时异常
    }
}
//自定义一个UncaughtExceptionHandler
class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("caught "+e);
    }
}
//一个可以创建新线程的工厂类
class HandleThreadFactory implements ThreadFactory{

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        return thread;
    }
}
public class ExceptionCatch {
    public static void main(String[] args) {
        /*
        * Executors.newCachedThreadPool() 如果没有参数就默认取Executors.defaultThreadFactory()
        * */
        ExecutorService exec = Executors.newCachedThreadPool(new HandleThreadFactory());
        /*
        * exec.execute 实际上是 ThreadPoolExecutor类调用execute方法，execute方法中调用 addWorker(command, true)，
        *       其中command是传入的 Runnable(就是new ExceptionThread2())，addWorker(Runnable firstTask, boolean core)中
        *       有 w = new Worker(firstTask);Thread t = w.thread;而 new Worker(firstTask)中有this.firstTask = firstTask;
        *       this.thread = getThreadFactory().newThread(this);而Worker这个类本神也实现了Runnable，它的run方法调用了
        *       this.firstTask的方法。其中getThreadFactory()就是我们传入的ThreadFactory。
        *       总结一下，exec.execute方法会调用ThreadFactory中的newThread方法，并将参数Runnable 包装传给newThread方法，并
        *           调用thread.start()
        * */
        exec.execute(new ExceptionThread2());
    }
}
