package com.expect.admin.data.pojo;

import java.util.List;

/**
 * description:
 * Created by gaoyw on 2018/5/13.
 */
public class WfDetail {
    String id;
    String name;
    String description;
    String type;
    List<WfpInfo> wfpInfos;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<WfpInfo> getWfpInfos() {
        return wfpInfos;
    }

    public void setWfpInfos(List<WfpInfo> wfpInfos) {
        this.wfpInfos = wfpInfos;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
