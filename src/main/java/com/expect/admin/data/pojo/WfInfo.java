package com.expect.admin.data.pojo;

/**
 * description:
 * Created by gaoyw on 2018/5/13.
 */
public class WfInfo {
    String id;
    String name;
    String typeName;
    boolean candele;
    String description;

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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isCandele() {
        return candele;
    }

    public void setCandele(boolean candele) {
        this.candele = candele;
    }
}
