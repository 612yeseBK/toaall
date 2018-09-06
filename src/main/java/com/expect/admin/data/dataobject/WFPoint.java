package com.expect.admin.data.dataobject;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * description:workflowpoint,属于可增加和删除的流程节点，仅用于需求变更后的情况，让用户可以修改流程
 * 关于终止节点：需要为某一个流程的开始和结束节点设置一个终结点，其名为“end_节点_end”,这个节点名称是不可以被添加作为实际节点的
 * 由于我这里使用了双向关联，所以只有一个外键，因此不得不为每个流程配置一个终止节点，但并不增加时间开销
 * Created by gaoyw on 2018/5/6.
 */
@Entity
@Table(name = "wf_point")
public class WFPoint {
    public static final String ENDPOINT = "end_节点_end";
    private String id;
    private String name;//流程节点名称
    private WorkFlow workFlow;//该节点对应的流程
    private WFPoint prePoint;//该节点的前一个节点,需要为开始节点在数据库里加入一个终结点
    private WFPoint nxtPoint;//该节点的后一个节点，需要为结束节点在数据库里加入一个终结点，这两个节点可以变成一个
    private Role role; //该节点的角色

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

    @Column(name = "name", length = 20)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "wf_id")
    public WorkFlow getWorkFlow() {
        return workFlow;
    }

    public void setWorkFlow(WorkFlow workFlow) {
        this.workFlow = workFlow;
    }

    @OneToOne
    @JoinColumn(name = "prp_id")
    public WFPoint getPrePoint() {
        return prePoint;
    }

    public void setPrePoint(WFPoint prePoint) {
        this.prePoint = prePoint;
    }

    @OneToOne(mappedBy = "prePoint") //节点关联时只需要做前置节点关联即可
    public WFPoint getNxtPoint() {
        return nxtPoint;
    }

    public void setNxtPoint(WFPoint nxtPoint) {
        this.nxtPoint = nxtPoint;
    }

    @OneToOne
    @JoinColumn(name = "role_id")
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
