package com.expect.admin.service.vo;

import com.expect.admin.data.dataobject.*;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * description:人员调用表的值对象
 * Created by gaoyw on 2018/5/6.
 */
public class TransferPersonnelVo {
    private String id;
    private String userName;//借调申请人
    private String staffNum;//职工号
    private String applyTime;//申请发起时间
    private String beginTime;//开始的时间
    private String endTime;//结束的时间（开始到结束之间就是借调时间）
    private String reason;//借调事由
    private int number;//借调人数
    private String toDepartment;//派遣前往的单位
    private String job;//派遣的工种
    private String state;//状态

    public TransferPersonnelVo(){}

    /**
     * 将do转成vo
     * @param tp
     * @return
     */
    public static TransferPersonnelVo convert(TransferPersonnel tp){
        TransferPersonnelVo tpVo = new TransferPersonnelVo();
        tpVo.id = tp.getId();
        tpVo.staffNum = tp.getStaffNum();
        tpVo.number = tp.getNumber();
        tpVo.toDepartment = tp.getToDepartment();
        tpVo.job = tp.getJob();
        tpVo.applyTime = tp.getApplyTime();
        tpVo.beginTime = tp.getBeginTime();
        tpVo.endTime =  tp.getEndTime();
        tpVo.reason = tp.getReason();
        if (!tp.getNxtPoint().getName().equals(WFPoint.ENDPOINT)){
            tpVo.state = tp.getNxtPoint().getName();
        } else if (tp.getState().equals(TransferPersonnel.PASSED)){
            tpVo.state = "已通过";
        } else {
            tpVo.state = "未通过";
        }
//        if (tp.getState().equals(TransferPersonnel.WAITING)){
//            tpVo.state = "待审批";
//        } else if (tp.getState().equals(TransferPersonnel.NOTCOM)){
//            tpVo.state = "未提交";
//        } else if (tp.getState().equals(TransferPersonnel.PASSED)){
//            tpVo.state = "已通过";
//        } else {
//            tpVo.state = "未通过";
//        }
        tpVo.userName = tp.getApplicant().getUsername();
        return tpVo;
    }

    /**
     * 将Vo转成do
     * @param transferPersonnelVo
     * @return
     */
    public static TransferPersonnel convert(TransferPersonnelVo transferPersonnelVo){
        TransferPersonnel transferPersonnel = new TransferPersonnel();
        BeanUtils.copyProperties(transferPersonnelVo,transferPersonnel);
        return transferPersonnel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getToDepartment() {
        return toDepartment;
    }

    public void setToDepartment(String toDepartment) {
        this.toDepartment = toDepartment;
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

    public String getStaffNum() {
        return staffNum;
    }

    public void setStaffNum(String staffNum) {
        this.staffNum = staffNum;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
