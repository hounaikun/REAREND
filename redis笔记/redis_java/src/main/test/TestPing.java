import redis.clients.jedis.Jedis;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-28 10:03
 **/
public class TestPing {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1",6379);
        System.out.println(jedis.ping());
    }
}
