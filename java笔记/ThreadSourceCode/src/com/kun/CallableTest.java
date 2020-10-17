package com.kun;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-16 13:49
 **/
class MyCallable implements Callable<String>{
    private int id;

    public MyCallable(int id) {
        this.id = id;
    }

    @Override
    public String call() throws Exception {
        return "result of MyCallable " + this.id;
    }
}
public class CallableTest {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ArrayList<Future<String>> results = new ArrayList<>();
        for(int i =0;i<10;i++){
            //submit()方法会产生Future对象，它用Callable返回结果的特定类型进行了参数化
            /*Future：
            *   表示异步计算的结果。 提供了一些方法来检查计算是否完成，等待其完成以及检索计算结果。
            *   只有在计算完成后才可以使用get方法检索结果，必要时将阻塞直到准备好为止
            * 假如有一个方法fun，计算过程可能非常耗时，等待fun返回，显然不明智的。可以在调用fun的时候，
            *   立马返回一个Future，可以通过Future这个数据结构去控制方法f的计算过程。
            *   这里的控制包括：
            *       get方法：获取计算结果（如果还没计算完，也是必须等待的）
            *       cancel方法：还没计算完，可以取消计算过程
            *       isDone方法：判断是否计算完
            *       isCancelled方法：判断计算是否被取消
            * */
            results.add(executorService.submit(new MyCallable(i)));
        }
        for(Future<String> fs : results){
            try {
                System.out.println(fs.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } finally {
                executorService.shutdown();
            }
        }

    }
}
