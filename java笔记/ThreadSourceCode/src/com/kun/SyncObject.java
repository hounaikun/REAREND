package com.kun;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-23 10:52
 **/
class DualSync {
    private Object syncObject = new Object();
    public synchronized void f(){ //这种是将当前对象当作锁
        for (int i = 0; i < 5; i++) {
            System.out.println("f()");
            Thread.yield();
        }
    }
    public void g(){
        synchronized (syncObject){ //这种是将syncObject对象当作锁
            for (int i = 0; i < 5; i++) {
                System.out.println("g()");
                Thread.yield();
            }
        }
    }
}
public class SyncObject {
    public static void main(String[] args) {
        DualSync dualSync = new DualSync();
        new Thread(){
            @Override
            public void run() {
                dualSync.f();
            }
        }.start();
        dualSync.g();
    }
}
