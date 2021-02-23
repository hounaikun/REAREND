package com.atguigu.nio;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2021-02-22 17:13
 **/
public class TestBuffer2021 {
    @Test
    public void test01() {
        String str = "sdk";
        ByteBuffer bb = ByteBuffer.allocate(1024);
        soutBuffer(bb);
        bb.put(str.getBytes());
        soutBuffer(bb);
        bb.flip();
        soutBuffer(bb);
        byte[] b = new byte[bb.limit()];
        bb.get(b,0,bb.limit());
        System.out.println(new String(b,0,bb.limit()));
        soutBuffer(bb);
    }

    private void soutBuffer(ByteBuffer bb) {
        System.out.println("capacity " + bb.capacity());
        System.out.println("limit " + bb.limit());
        System.out.println("position " + bb.position());
        System.out.println("-----------------");
    }
}
