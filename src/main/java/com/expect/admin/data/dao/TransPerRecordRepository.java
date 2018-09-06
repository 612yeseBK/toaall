package com.expect.admin.data.dao;

import com.expect.admin.data.dataobject.TransPerRecord;
import com.expect.admin.data.dataobject.TransferPersonnel;
import com.expect.admin.data.dataobject.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * description:
 * Created by gaoyw on 2018/5/6.
 */
public interface TransPerRecordRepository extends JpaRepository<TransPerRecord, String> {
    @Query("select tpr from TransPerRecord tpr where tpr.user.id = ?1 order by tpr.clsj desc")
    List<TransPerRecord> findByUserId(String usrId);

    @Query("select tpr from TransPerRecord tpr where tpr.user.id = ?1 and tpr.transferPersonnel.id = ?2 order by tpr.clsj desc")
    TransPerRecord findByUserAndTransferPersonnel(String usrid, String tpid);

    List<TransPerRecord> findByUser(User user);
}
