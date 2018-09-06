package com.expect.admin.data.dataobject;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.sql.Date;

/**
 * description:这里是人员借调申请的审批流程记录表
 * 由于这里采用了一种新的配置方法，所有就不再加入到原有的lcrzb里面了
 * Created by gaoyw on 2018/5/6.
 */
@Entity
@Table(name = "transper_record")
public class TransPerRecord {

    private String id;

    private User user;//处理人

    private String clsj;//处理时间

    private String message;//处理意见

    private String cljg;//处理结果

    private TransferPersonnel transferPersonnel;//处理内容的id（所处理的东西的id）

    private WFPoint wfPoint;

    public TransPerRecord(){}

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
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "clsj")
    public String getClsj() {
        return clsj;
    }

    public void setClsj(String clsj) {
        this.clsj = clsj;
    }

    @Column(name = "message", length = 200)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Column(name = "cljg", length = 20)
    public String getCljg() {
        return cljg;
    }

    public void setCljg(String cljg) {
        this.cljg = cljg;
    }

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "transp_id")
    public TransferPersonnel getTransferPersonnel() {
        return transferPersonnel;
    }

    public void setTransferPersonnel(TransferPersonnel transferPersonnel) {
        this.transferPersonnel = transferPersonnel;
    }


    @ManyToOne
    @JoinColumn(name = "wfp_id")
    public WFPoint getWfPoint() {
        return wfPoint;
    }

    public void setWfPoint(WFPoint wfPoint) {
        this.wfPoint = wfPoint;
    }
}
