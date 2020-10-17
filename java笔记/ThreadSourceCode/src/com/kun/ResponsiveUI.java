package com.kun;


import java.io.IOException;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-09-16 17:42
 **/
public class ResponsiveUI extends Thread {
    private static volatile double d = 1;

    @Override
    public void run() {
        while (true){
            d = d + (Math.PI + Math.E) /d;
        }
    }

    public ResponsiveUI() {
        setDaemon(true);
        start();
    }

    public static void main(String[] args) throws IOException {
        new ResponsiveUI();
        byte[] inByte = new byte[1024];
        System.in.read(inByte);
        String inStr = new String(inByte);
        System.out.println(d+" "+inStr);
    }
}
