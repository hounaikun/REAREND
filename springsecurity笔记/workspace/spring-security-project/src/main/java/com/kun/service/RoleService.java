package com.kun.service;


import com.kun.entity.SysRole;
import org.springframework.stereotype.Service;

import java.util.List;

public interface RoleService {

    public void save(SysRole role);

    public List<SysRole> findAll();

}
