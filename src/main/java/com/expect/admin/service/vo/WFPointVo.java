package com.expect.admin.service.vo;

import com.expect.admin.data.dataobject.User;
import com.expect.admin.data.dataobject.WFPoint;
import com.expect.admin.data.dataobject.WorkFlow;

import java.util.HashSet;
import java.util.Set;

/**
 * description:
 * Created by gaoyw on 2018/5/6.
 */
public class WFPointVo {
    private String id;
    private String name;//流程节点名称
    private WorkFlowVo workFlowVo;//该节点对应的流程
    private WFPointVo prePointVo;//该节点的前一个节点,需要为开始节点在数据库里加入一个终结点
    private WFPointVo nxtPointVo;//该节点的后一个节点，需要为结束节点在数据库里加入一个终结点，这两个节点可以变成一个
    private Set<UserVo> userVos = new HashSet<>();//该节点的审批人员

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WorkFlowVo getWorkFlowVo() {
        return workFlowVo;
    }

    public void setWorkFlowVo(WorkFlowVo workFlowVo) {
        this.workFlowVo = workFlowVo;
    }

    public WFPointVo getPrePointVo() {
        return prePointVo;
    }

    public void setPrePointVo(WFPointVo prePointVo) {
        this.prePointVo = prePointVo;
    }

    public WFPointVo getNxtPointVo() {
        return nxtPointVo;
    }

    public void setNxtPointVo(WFPointVo nxtPointVo) {
        this.nxtPointVo = nxtPointVo;
    }

    public Set<UserVo> getUserVos() {
        return userVos;
    }

    public void setUserVos(Set<UserVo> userVos) {
        this.userVos = userVos;
    }
}
