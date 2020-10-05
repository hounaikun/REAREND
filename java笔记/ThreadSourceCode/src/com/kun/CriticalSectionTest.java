package com.kun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-21 14:33
 **/
/*一个线程不安全的类
 * */
class Pair {
    private int x, y;

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Pair() {
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void incrementX() {
        x++;
    }

    public void incrementY() {
        y++;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    //一个运行时异常
    public class PairValuesNotEqualException extends RuntimeException {
        public PairValuesNotEqualException() {
            super("Pair values not equal：" + Pair.this);
        }
    }

    //x!=y时，抛出运行时异常
    public void checkState() {
        if (x != y) {
            throw new PairValuesNotEqualException();
        }
    }
}

/*一个线程安全类，使其包含Pair
 * */
abstract class PairManager {
    AtomicInteger checkCounter = new AtomicInteger(0);
    protected Pair p = new Pair();
    private List<Pair> storage = Collections.synchronizedList(new ArrayList<>());
    public synchronized Pair getPair(){
        return new Pair(p.getX(),p.getY());
    }
    protected void store(Pair pair){
        storage.add(pair);
        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException e) {
        }
    }
    public abstract void increment();
}
/*PairManager的子类
* */
class PairManagerChild01 extends PairManager{
    @Override
    public void increment() {
        p.incrementX();
        p.incrementY();
        store(getPair());
    }
}
class PairManagerChild02 extends PairManager{
    @Override
    public void increment() {
        Pair temp;
        synchronized (this){
            p.incrementX();
            p.incrementY();
            temp = getPair();
        }
        store(temp);
    }
}
//启动一个线程
class PairManipulator implements Runnable{

    @Override
    public void run() {

    }
}
public class CriticalSectionTest {

}
