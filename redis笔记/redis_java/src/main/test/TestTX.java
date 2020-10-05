import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-28 10:03
 **/
public class TestTX {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1",6379);
        Transaction transaction = jedis.multi();
        /*日常操作*/
        transaction.set("k1","v1");
        transaction.set("k2","v2");
        transaction.set("k3","v3");

        transaction.exec(); //执行
        transaction.discard(); //回滚
        /*加锁*/

    }
}
