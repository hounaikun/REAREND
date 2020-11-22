package com.kun.springcloud.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description: <p>支付</p>
 * @author: hounaikun
 * @create: 2020-11-04 22:50
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment implements Serializable {
    private static final long serialVersionUID = -3135469450465326264L;
    private Long id;
    private String serial;
}
