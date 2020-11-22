package com.kun.cache.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-11-17 23:15
 **/
@Data
@AllArgsConstructor
public class Employee {
    private Integer id;
    private String lastName;
    private String email;
    private Integer gender;//性别 1男 2女
    private Integer dId;
}
