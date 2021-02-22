package com.juc.Six_Callable接口;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2021-01-13 23:57
 **/
public class CallableTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<String> ft = new FutureTask<String>(() -> {
            TimeUnit.SECONDS.sleep(2);
            return "线程"+Thread.currentThread().getName()+" 完成";
        });
        System.out.println("主线程在跑。。。。。。。。。。。");
        new Thread(ft,"A").start();
        while(!ft.isDone()) {
            TimeUnit.SECONDS.sleep(1);
            System.out.println("线程A 运行中。。。");
        }
        System.out.println(ft.get());
    }
}
