package com.kun.bean;

import java.io.Serializable;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-01 18:58
 **/
public class KTest implements Serializable {

    private static final long serialVersionUID = -5843180384995152179L;
    private Integer id;
    private String userName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "KTest{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                '}';
    }
}
