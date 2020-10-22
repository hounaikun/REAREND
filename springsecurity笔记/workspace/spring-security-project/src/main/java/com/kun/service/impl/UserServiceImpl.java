package com.kun.service.impl;

import com.kun.dao.UserDao;
import com.kun.entity.SysUser;
import com.kun.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-20 21:59
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Override
    public void save(SysUser user) {

    }

    @Override
    public List<SysUser> findAll() {
        return null;
    }

    @Override
    public Map<String, Object> toAddRolePage(Integer id) {
        return null;
    }

    @Override
    public void addRoleToUser(Integer userId, Integer[] ids) {

    }

    /**
     * 认证业务
     * @param username 用户在浏览器输入的用户名
     * @return UserDetails 是springSecurity 自己的用户对象
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            //根据用户名做查询
            SysUser sysUser = userDao.findByName(username);
            if(sysUser == null){
                //若用户名不对，直接返回null，表示认证失败。
                return null;
            }
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            //{noop} 后面的密码，springSecurity会认为是原文。{noop}表示不加密认证。
            UserDetails userDetails = new User(username,"{noop}"+sysUser.getPassword(),authorities);
            return userDetails;
        }catch (Exception e){
            e.printStackTrace();
            //springSecurity 认为只要返回null，就是认证失败
            return null;
        }
    }
}
