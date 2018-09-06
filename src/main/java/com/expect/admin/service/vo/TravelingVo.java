package com.expect.admin.service.vo;


import com.expect.admin.data.dataobject.Traveling;
import com.expect.admin.utils.DateUtil;

import java.util.Date;
import java.util.List;


public class TravelingVo {
    private String sbd;
    private String id;
    private String name; //姓名
    private String sex; //性别
    private String birth; //出生年月
    private String staffId; //职工号
    private String position; //岗位
    private String workUnit; //工作单位
    private String travelingTime; //出差时间
    private String reasonNumber; //出差事由及人数
    private String userName; //拟出差人姓名
    private String nccrid; //拟出差人ID
//    private String ccfl; //出差分类
    private String ccshzt; //出差审核状态
    private String sqsj; //申请时间
    private List<AttachmentVo> attachmentList;//附件Vo
    private List<LcrzbVo> lcrzList;//流程日志
    private String lcbs;//流程标识
    private String spyj;//审批意见
    private String dyrq;//打印日期
    private String dateFrom; //出差开始时间
    private String dateTo; //出差结束时间

    public TravelingVo(){

    }

    public TravelingVo(Traveling traveling){
        this.sbd = traveling.getSbd();
        this.id = traveling.getId();
        this.name = traveling.getName();
        this.sex = traveling.getSex();
        this.birth = traveling.getBirth();
        this.staffId = traveling.getStaffId();
        this.position = traveling.getPosition();
        this.workUnit = traveling.getWorkUnit();
        this.travelingTime = traveling.getTravelingTime();
        this.reasonNumber = traveling.getReasonNumber();
        this.nccrid = traveling.getNccr().getId();
        this.ccshzt = traveling.getCcshzt();
        if(traveling.getSqsj() != null){
            this.sqsj = DateUtil.format(traveling.getSqsj(), DateUtil.fullFormat);
        }
        this.lcbs = traveling.getLcbs();
        this.dyrq = traveling.getDyrq();
        this.dateFrom = traveling.getTravelingTime().split("-")[0];
        this.dateTo = traveling.getTravelingTime().split("-")[1];
    }

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getWorkUnit() {
        return workUnit;
    }

    public void setWorkUnit(String workUnit) {
        this.workUnit = workUnit;
    }

    public String getTravelingTime() {
        return travelingTime;
    }

    public void setTravelingTime(String travelingTime) {
        this.travelingTime = travelingTime;
    }

    public String getReasonNumber() {
        return reasonNumber;
    }

    public void setReasonNumber(String reasonNumber) {
        this.reasonNumber = reasonNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNccrid() {
        return nccrid;
    }

    public void setNccrid(String nccrid) {
        this.nccrid = nccrid;
    }

//    public String getCcfl() {
//        return ccfl;
//    }
//
//    public void setCcfl(String ccfl) {
//        this.ccfl = ccfl;
//    }

    public String getCcshzt() {
        return ccshzt;
    }

    public void setCcshzt(String ccshzt) {
        this.ccshzt = ccshzt;
    }

    public String getSqsj() {
        return sqsj;
    }

    public void setSqsj(String sqsj) {
        this.sqsj = sqsj;
    }

    public List<AttachmentVo> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<AttachmentVo> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public List<LcrzbVo> getLcrzList() {
        return lcrzList;
    }

    public void setLcrzList(List<LcrzbVo> lcrzList) {
        this.lcrzList = lcrzList;
    }

    public String getLcbs() {
        return lcbs;
    }

    public void setLcbs(String lcbs) {
        this.lcbs = lcbs;
    }

    public String getSpyj() {
        return spyj;
    }

    public void setSpyj(String spyj) {
        this.spyj = spyj;
    }

    public String getDyrq() {
        return dyrq;
    }

    public void setDyrq(String dyrq) {
        this.dyrq = dyrq;
    }

    public String getSbd() {
        return sbd;
    }

    public void setSbd(String sbd) {
        this.sbd = sbd;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }
}
