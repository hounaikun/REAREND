package com.kun.dao;

import com.kun.bean.KTest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestDao {
    List<KTest> getAllMessage();
}
