package com.kun.service.impl;

import com.kun.dao.UserDao;
import com.kun.entity.SysRole;
import com.kun.entity.SysUser;
import com.kun.service.RoleService;
import com.kun.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-20 21:59
 **/
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void save(SysUser user) {
        //对密码进行加密，然后再入库
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(user);

    }

    @Override
    public List<SysUser> findAll() {
        return userDao.findAll();
    }

    @Override
    public Map<String, Object> toAddRolePage(Integer id) {
        List<Integer> myRoles = userDao.findRolesByUid(id);
        List<SysRole> allRoles = roleService.findAll();
        Map<String,Object> map = new HashMap<>();
        map.put("allRoles",allRoles);
        map.put("myRoles",myRoles);
        return map;
    }

    @Override
    public void addRoleToUser(Integer userId, Integer[] ids) {
        for(int i =0;i< ids.length;i++){
            userDao.addRoles(userId,ids[i]);
        }
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
            //authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            //从用户角色表查询当前用户有哪些角色，然后添加进去
            List<SysRole> roles = sysUser.getRoles();
            for (SysRole role : roles) {
                authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
            }
            //{noop} 后面的密码，springSecurity会认为是原文。{noop}表示不加密认证。
            UserDetails userDetails = new User(
                    username,
                    sysUser.getPassword(),
                    sysUser.getStatus() == 1,
                    true,
                    true,
                    true,
                    authorities);
            return userDetails;
        }catch (Exception e){
            e.printStackTrace();
            //springSecurity 认为只要返回null，就是认证失败
            return null;
        }
    }
}
