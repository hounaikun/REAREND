package com.kun.springcloud.myhandler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.kun.springcloud.entities.CommonResult;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-11-22 22:04
 **/
public class CustomerBlockHandler {
    public static CommonResult handleException(BlockException exception) {
        return new CommonResult(2020, "自定义限流处理信息....CustomerBlockHandler");

    }
}
