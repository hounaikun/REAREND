package com.kun;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-14 16:49
 **/
public class ExecutorTest {
    public static void main(String[] args) {
        //ExecutorService:具有服务生命周期的Executor，例如关闭
        /* newCachedThreadPool():创建一个线程池，该线程池根据需要创建新线程，但是将在先前构造的线程可用时重用它们。
            但是此种方式通常会创建与所需数量相同的线程，然后在它回收旧线程时停止创建新线程，因此它是首选*/
//        ExecutorService executorService = Executors.newCachedThreadPool();

        //一次性预先执行代价高昂的线程分配，可以限制线程的数量
        /* newFixedThreadPool(1)：创建一个线程池，该线程池重用在共享的无边界队列上操作的固定数量的线程。
           如果线程池中所有的线程都处在活动状态，此时在提交任务，就会在队列中等待，知道有空闲的进程 */
//        ExecutorService executorService = Executors.newFixedThreadPool(1);

        //newSingleThreadExecutor():创建线程数量为1的FixedThreadPool
        /* 如果向SingleThreadExecutor提交了多个任务，那么这些任务将排队，每个任务都会在下个任务开始之前运行结束，
           所有的任务将使用相同的进程 */
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        for(int i =0;i<5;i++){
            executorService.execute(new LiftOff());
        }
        //启动有序关闭，其中先前提交的任务将被执行，但不会接受任何新任务。 如果已经关闭，调用没有额外的作用。
        executorService.shutdown();
    }
}
