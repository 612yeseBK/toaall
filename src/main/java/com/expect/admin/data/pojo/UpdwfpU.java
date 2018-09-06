package com.expect.admin.data.pojo;

import java.util.List;

/**
 * description:用于进行节点人员调整
 * Created by gaoyw on 2018/5/13.
 */
public class UpdwfpU {

    String wfpid;//流程节点id
    List<String> addUsrIds;//需要对节点新增的usrid
    List<String> delUsrIds;//需要对节点删除的usrid

    public String getWfpid() {
        return wfpid;
    }

    public void setWfpid(String wfpid) {
        this.wfpid = wfpid;
    }

    public List<String> getAddUsrIds() {
        return addUsrIds;
    }

    public void setAddUsrIds(List<String> addUsrIds) {
        this.addUsrIds = addUsrIds;
    }

    public List<String> getDelUsrIds() {
        return delUsrIds;
    }

    public void setDelUsrIds(List<String> delUsrIds) {
        this.delUsrIds = delUsrIds;
    }
}
