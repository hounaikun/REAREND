package com.kun.entity;

import lombok.Data;

import java.util.List;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-18 22:25
 **/
@Data
public class SysUser {
    private Integer id;
    private String username;
    private String password;
    private Integer status;
    private List<SysRole> roles;
}
