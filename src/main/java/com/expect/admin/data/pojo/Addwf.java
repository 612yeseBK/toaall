package com.expect.admin.data.pojo;

import java.util.List;

/**
 * description:这个对象用于从前端接收新增流程的工具
 * Created by gaoyw on 2018/5/6.
 */
public class Addwf {
    private String name;//流程名称
    private String type;//流程类别
    private String description;//节点描述
    private List<String> wfpnames;//该流程的流程节点们，list的顺序就是流程节点的顺序
    private List<String> funcIds;//不同流程节点对应的角色功能，这两个list长度必须相同,其一一对应

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getWfpnames() {
        return wfpnames;
    }

    public void setWfpnames(List<String> wfpnames) {
        this.wfpnames = wfpnames;
    }

    public List<String> getFuncIds() {
        return funcIds;
    }

    public void setFuncIds(List<String> funcIds) {
        this.funcIds = funcIds;
    }
}
