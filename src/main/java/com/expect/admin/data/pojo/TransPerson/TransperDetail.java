package com.expect.admin.data.pojo.TransPerson;

import com.expect.admin.data.dataobject.*;
import com.expect.admin.service.vo.AttachmentVo;
import com.expect.admin.service.vo.TransPerRecordVo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * description:
 * Created by gaoyw on 2018/5/28.
 */
public class TransperDetail {
    private String id;
    private String staffNum;//职工号
    private String applyTime;//申请发起时间
    private String beginTime;//开始的时间  yyymmdd
    private String endTime;//结束的时间（开始到结束之间就是借调时间）
    private String reason;//借调事由
    private int number;//借调人数
    private String toDepartment;//派遣前往的单位
    private String job;//派遣的工种
    private List<Front_TransRec> front_transRecs ;//审批流程日志
    private List<AttachmentVo> attachmentVos;//附件

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStaffNum() {
        return staffNum;
    }

    public void setStaffNum(String staffNum) {
        this.staffNum = staffNum;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getToDepartment() {
        return toDepartment;
    }

    public void setToDepartment(String toDepartment) {
        this.toDepartment = toDepartment;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public List<Front_TransRec> getFront_transRecs() {
        return front_transRecs;
    }

    public void setFront_transRecs(List<Front_TransRec> front_transRecs) {
        this.front_transRecs = front_transRecs;
    }

    public List<AttachmentVo> getAttachmentVos() {
        return attachmentVos;
    }

    public void setAttachmentVos(List<AttachmentVo> attachmentVos) {
        this.attachmentVos = attachmentVos;
    }
}
