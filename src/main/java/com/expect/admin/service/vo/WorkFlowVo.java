package com.expect.admin.service.vo;

import com.expect.admin.data.dataobject.WorkFlow;

/**
 * description:
 * Created by gaoyw on 2018/5/6.
 */
public class WorkFlowVo {
    private String id;
    private String type;//流程的类别，是属于借调，还是出差
    private String name;//流程的名称
    private String description;//流程描述

    public WorkFlowVo(){}

    public WorkFlowVo(WorkFlow workFlow){
        this.description = workFlow.getDescription();
        this.id = workFlow.getId();
        this.name = workFlow.getName();
        this.type = workFlow.getType();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
