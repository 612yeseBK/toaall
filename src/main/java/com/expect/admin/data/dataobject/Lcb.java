package com.expect.admin.data.dataobject;

import java.util.Date;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;

import com.expect.admin.service.vo.LcVo;
import com.expect.admin.utils.DateUtil;
import com.expect.admin.utils.StringUtil;
import org.hibernate.annotations.GenericGenerator;

/**
 * 流程表
 * @author zcz
 *
 */
@Entity
@Table(name = "lcb")
public class Lcb {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(name = "id", nullable = false, unique = true, length = 32)
	private String id;//流程标识
	
	@Column(name = "lcName", length = 30)
	private String lcName;//流程名称

	@Column(name = "lclb", length = 20)
	private String lclb; //流程类别

	@Column(name = "description", length = 255)
	private String description;

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinColumn(name = "cjz_id")
	private User cjz;//创建者
	
	@Column(name = "cjsj")
	private Date cjsj;//创建时间
	
	@Column(name = "zt", length = 2)
	private String zt;//状态（Y 启用， N 不启用）

	@Column(name = "lcfqRole")
	private String lcfqRole; //流程发起的角色

	public Lcb(){}

	public Lcb(LcVo lcVo){
		this.id = lcVo.getId();
		this.lcName = lcVo.getLcName();
		this.lclb = lcVo.getLclb();
		this.description = lcVo.getDescription();
		if(StringUtil.isBlank(lcVo.getCjsj())){
			this.cjsj = new Date();
		}else{
			this.cjsj = DateUtil.parse(lcVo.getCjsj(), DateUtil.fullFormat);
		}
		this.zt = lcVo.getZt();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getCjz() {
		return cjz;
	}

	public void setCjz(User cjz) {
		this.cjz = cjz;
	}

	public Date getCjsj() {
		return cjsj;
	}

	public void setCjsj(Date cjsj) {
		this.cjsj = cjsj;
	}

	public String getLcName() {
		return lcName;
	}

	public void setLcName(String lcName) {
		this.lcName = lcName;
	}

	public String getLclb() {
		return lclb;
	}

	public void setLclb(String lclb) {
		this.lclb = lclb;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getZt() {
		return zt;
	}

	public void setZt(String zt) {
		this.zt = zt;
	}

	public String getLcfqRole() {
		return lcfqRole;
	}

	public void setLcfqRole(String lcfqRole) {
		this.lcfqRole = lcfqRole;
	}
}
