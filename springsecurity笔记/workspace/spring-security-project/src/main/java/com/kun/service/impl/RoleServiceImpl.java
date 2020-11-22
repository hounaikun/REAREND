package com.kun.service.impl;

import com.kun.dao.RoleDao;
import com.kun.entity.SysRole;
import com.kun.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-22 21:46
 **/
@Service("roleService")
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleDao roleDao;

    @Override
    public void save(SysRole role) {
        roleDao.save(role);
    }

    @Override
    public List<SysRole> findAll() {
        return roleDao.findAll();
    }
}
