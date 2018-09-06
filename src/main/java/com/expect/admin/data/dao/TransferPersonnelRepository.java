package com.expect.admin.data.dao;

import com.expect.admin.data.dataobject.TransferPersonnel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * description:
 * Created by gaoyw on 2018/5/6.
 */
public interface TransferPersonnelRepository extends JpaRepository<TransferPersonnel, String> {
    /**
     * 通过id获取相应的申请记录
     * @param id
     * @return
     */
    public TransferPersonnel findById(String id);


}
