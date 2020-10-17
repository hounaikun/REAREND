package connnection;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-14 14:30
 **/
public class ZookeeperConnection {
    public static void main(String[] args) {
        try {
            /*
            * countDownLatch这个类使一个线程等待其他线程各自执行完毕后再执行。
                是通过一个计数器来实现的，计数器的初始值是线程的数量。每当一个线程执行完毕后，
                计数器的值就-1，当计数器的值为0时，表示所有线程都执行完毕，然后在闭锁上等待的线程就可以恢复工作了。
            * countDownLatch类中只提供了一个构造器：public CountDownLatch(int count) {  };
            * 类中有三个方法是最重要的：
                 //调用await()方法的线程会被挂起，它会等待直到count值为0才继续执行
                public void await() throws InterruptedException { };
                //和await()类似，只不过等待一定的时间后count值还没变为0的话就会继续执行
                public boolean await(long timeout, TimeUnit unit) throws InterruptedException { };
                //将count值减1
                public void countDown() { };
            * */
            CountDownLatch countDownLatch = new CountDownLatch(1);

            // arg1:服务器的ip和端口
            // arg2:客户端与服务器之间的会话超时时间 以毫秒为单位的
            // arg3:监视器对象
            ZooKeeper zookeeper = new ZooKeeper("121.199.70.188",
                    5000,
                    new Watcher() {
                        public void process(WatchedEvent watchedEvent) {
                            if(watchedEvent.getState()==Event.KeeperState.SyncConnected){
                                System.out.println("连接创建成功!");
                                countDownLatch.countDown();//放行
                            }
                        }
                    });
            //阻塞当前线程，直到计数器的值为0
            countDownLatch.await();
            // 会话编号
            System.out.println(zookeeper.getSessionId());
            zookeeper.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
