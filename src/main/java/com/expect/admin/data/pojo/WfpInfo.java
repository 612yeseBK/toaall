package com.expect.admin.data.pojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description:
 * Created by gaoyw on 2018/5/13.
 */
public class WfpInfo {
    String id;
    String name;
    String[] funcId;
    List<IdName> usersInfos;

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

    public List<IdName> getUsersInfos() {
        return usersInfos;
    }

    public void setUsersInfos(List<IdName> usersInfos) {
        this.usersInfos = usersInfos;
    }


    public String[] getFuncId() {
        return funcId;
    }

    public void setFuncId(String[] funcId) {
        this.funcId = funcId;
    }

}

