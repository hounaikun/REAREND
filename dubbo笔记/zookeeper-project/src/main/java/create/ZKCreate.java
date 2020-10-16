package create;

import org.apache.zookeeper.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-15 09:16
 **/
public class ZKCreate {
    private final String IP = "121.199.70.188:2181";
    ZooKeeper zooKeeper;
    @Before
    public void connection() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(IP, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功");
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
    }
    @After
    public void close() throws Exception {
        zooKeeper.close();
    }
    @Test
    public void create1() throws Exception {
        String s = zooKeeper.create("/node1", "node1".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(s);
    }
}
