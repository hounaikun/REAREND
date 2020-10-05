package com.kun.service.impl;

import com.kun.bean.KTest;
import com.kun.dao.TestDao;
import com.kun.service.TestService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("testService")
public class TestServiceImpl implements TestService {
    @Resource(name= "testDao")
    private TestDao testDao;

    @Override
    public List<KTest> getAllMessage() {
        return testDao.getAllMessage();
    }
}
