package com.expect.admin.data.pojo.TransPerson;

/**
 * description:
 * Created by gaoyw on 2018/5/28.
 */
public class TransAplyRecds {
    private String id;//申请记录
    private String jobKind;//申请的工种
    private String applyDate;//申请时间
    private String state;//审核状态

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobKind() {
        return jobKind;
    }

    public void setJobKind(String jobKind) {
        this.jobKind = jobKind;
    }

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
