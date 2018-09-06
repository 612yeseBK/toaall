package com.expect.admin.data.dao;

import com.expect.admin.data.dataobject.WorkFlow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * description:
 * Created by gaoyw on 2018/5/6.
 */
public interface WorkFlowRepository extends JpaRepository<WorkFlow, String> {
    List<WorkFlow> findAll();
    List<WorkFlow> findAllByType(String type);
}
