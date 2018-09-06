package com.expect.admin.data.dataobject;

import com.expect.admin.service.vo.TravelingVo;
import com.expect.admin.utils.DateUtil;
import com.expect.admin.utils.StringUtil;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "traveling_info")
public class Traveling {
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
    private User nccr; //拟出差人
//    private String ccfl; //出差分类
    private String lcbs;//流程标识
    private String ccshzt; //出差审核状态
    private Date sqsj; //申请时间
    private String dyrq;//打印日期
    private Set<Attachment> attachments;//附件
    private Set<Lcrzb> lcrzSet = new HashSet<>();//流程日志

    public Traveling(){

    }

    public Traveling(TravelingVo travelingVo){
        this.sbd = travelingVo.getSbd();
        this.id = travelingVo.getId();
        this.name = travelingVo.getName();
        this.sex = travelingVo.getSex();
        this.birth = travelingVo.getBirth();
        this.staffId = travelingVo.getStaffId();
        this.position = travelingVo.getPosition();
        this.workUnit = travelingVo.getWorkUnit();
        this.travelingTime = travelingVo.getDateFrom() + "-" + travelingVo.getDateTo();
        this.reasonNumber = travelingVo.getReasonNumber();

        this.lcbs = travelingVo.getLcbs();
        this.ccshzt = travelingVo.getCcshzt();
        if(StringUtil.isBlank(travelingVo.getSqsj())){
            this.sqsj = new Date();
        }else{
            this.sqsj = DateUtil.parse(travelingVo.getSqsj(), DateUtil.fullFormat);
        }
    }


    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, unique = true, length = 32)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "sbd", length = 20)
    public String getSbd() {
        return sbd;
    }

    public void setSbd(String sbd) {
        this.sbd = sbd;
    }

    @Column(name = "name", length = 32)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "sex", length = 10)
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Column(name = "birth", length = 32)
    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    @Column(name = "staffId", length = 32)
    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    @Column(name = "position", length = 32)
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Column(name = "workUnit", length = 32)
    public String getWorkUnit() {
        return workUnit;
    }

    public void setWorkUnit(String workUnit) {
        this.workUnit = workUnit;
    }

    @Column(name = "travelingTime", length = 32)
    public String getTravelingTime() {
        return travelingTime;
    }

    public void setTravelingTime(String travelingTime) {
        this.travelingTime = travelingTime;
    }

    @Column(name = "reasonNumber", length = 250)
    public String getReasonNumber() {
        return reasonNumber;
    }

    public void setReasonNumber(String reasonNumber) {
        this.reasonNumber = reasonNumber;
    }

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "nccr_id")
    public User getNccr() {
        return nccr;
    }

    public void setNccr(User nccr) {
        this.nccr = nccr;
    }

//    @Column(name = "ccfl", length = 10)
//    public String getCcfl() {
//        return ccfl;
//    }
//
//    public void setCcfl(String ccfl) {
//        this.ccfl = ccfl;
//    }

    @Column(name = "lcbs", length = 50)
    public String getLcbs() {
        return lcbs;
    }

    public void setLcbs(String lcbs) {
        this.lcbs = lcbs;
    }

    @Column(name = "ccshzt", length = 50)
    public String getCcshzt() {
        return ccshzt;
    }

    public void setCcshzt(String ccshzt) {
        this.ccshzt = ccshzt;
    }

    @Column(name = "sqsj")
    public Date getSqsj() {
        return sqsj;
    }

    public void setSqsj(Date sqsj) {
        this.sqsj = sqsj;
    }

    @Column(name = "dyrq")
    public String getDyrq() {
        return dyrq;
    }

    public void setDyrq(String dyrq) {
        this.dyrq = dyrq;
    }

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "traveling_attachment", joinColumns = @JoinColumn(name = "traveling_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id"))
    public Set<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
    }

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "traveling_lcrzb", joinColumns = @JoinColumn(name = "traveling_id"),
            inverseJoinColumns = @JoinColumn(name = "lcrzb_id"))
    public Set<Lcrzb> getLcrzSet() {
        return lcrzSet;
    }

    public void setLcrzSet(Set<Lcrzb> lcrzSet) {
        this.lcrzSet = lcrzSet;
    }
}
