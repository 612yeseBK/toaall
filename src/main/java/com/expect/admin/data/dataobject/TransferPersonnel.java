package com.expect.admin.data.dataobject;

import jdk.nashorn.internal.scripts.JO;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * description:人员借调表，用于公司申请人员借调使用
 * 5/13：公司给的表格数据有：姓名，性别，出生年月，职号，岗位，工作单位，借用时间，派往单位，工种，借用事由，借用人数
 * 5/13：其中姓名，性别，岗位，工作单位我们可以从User里面获取，其余需要用户填写，什么时候需要User的信息增加了，这里再进行调整
 * Created by gaoyw on 2018/5/5.
 */
@Entity
@Table(name = "c_transfer_personnel")
public class TransferPersonnel {
    //申请的四种状态
    public static final String NOTCOM = "notcom";//未提交
    public static final String WAITING= "waiting";//待处理
    public static final String PASSED = "passed";//已通过
    public static final String REJECTED = "rejected";//未通过
    private String id;
    private User applicant;//借调申请人
    private String staffNum;//职工号
    private String applyTime;//申请发起时间
    private String beginTime;//开始的时间  yyymmdd
    private String endTime;//结束的时间（开始到结束之间就是借调时间）
    private String reason;//借调事由
    private int number;//借调人数
    private String toDepartment;//派遣前往的单位
    private String job;//派遣的工种
    private WorkFlow workFlow;//该申请属于对应哪一个流程
    private WFPoint nxtPoint;//接下来要进入的流程节点，当前节点就是记录里面的节点，某个审批人审核之后，节点往后移动一个
    private String state;//流程的状态，共有四个，，待提交，待处理，通过，不通过
    private Set<TransPerRecord> transPerRecords = new HashSet<>();//审批流程日志
    private Set<Attachment> attachments;//附件



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

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "applicant_id")
    //需要做多对一双向关联，在该表生成applicant_id外键，还需在user表生成一个对象
    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    @Column(name = "staff_num")
    public String getStaffNum() {
        return staffNum;
    }

    public void setStaffNum(String staffNum) {
        this.staffNum = staffNum;
    }

    @Column(name = "apply_time")
    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    @Column(name = "begin_time")
    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    @Column(name = "end_time")
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Column(name = "reason")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Column(name = "number")
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Column(name = "department_id")
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

    @ManyToOne
    @JoinColumn(name = "wf_id")
    public WorkFlow getWorkFlow() {
        return workFlow;
    }

    public void setWorkFlow(WorkFlow workFlow) {
        this.workFlow = workFlow;
    }

    @OneToMany(mappedBy = "transferPersonnel", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    public Set<TransPerRecord> getTransPerRecords() {
        return transPerRecords;
    }

    public void setTransPerRecords(Set<TransPerRecord> transPerRecords) {
        this.transPerRecords = transPerRecords;
    }

    @OneToOne
    @JoinColumn(name = "nxt_wfpid")
    public WFPoint getNxtPoint() {
        return nxtPoint;
    }

    public void setNxtPoint(WFPoint nxtPoint) {
        this.nxtPoint = nxtPoint;
    }

    @Column(name = "state")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "s_transper_attachment", joinColumns = @JoinColumn(name = "transper_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id"))
    public Set<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
    }


}
