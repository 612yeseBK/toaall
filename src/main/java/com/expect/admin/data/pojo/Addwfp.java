package com.expect.admin.data.pojo;

import java.util.List;

/**
 * description:用于接受流程节点的信息
 * Created by gaoyw on 2018/5/6.
 */
public class Addwfp {
    private String id;//节点id
    private String name;//节点名称
    private List<String> ids;//该节点的所有用户id

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

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
