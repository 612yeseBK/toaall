package com.expect.admin.data.dao;

import com.expect.admin.data.dataobject.WFPoint;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * description:
 * Created by gaoyw on 2018/5/6.
 */
public interface WFPointRepository extends JpaRepository<WFPoint, String> {
    WFPoint findByName(String name);
}
