package com.expect.admin.service.vo;

import com.expect.admin.data.dataobject.Lcb;
import com.expect.admin.utils.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class LcVo {

	private String id;//流程标识
	private String lcName;//流程名称
	private String lclb; //流程类别
	private String description;
	private String cjz_id;//创建者
	private String cjsj;//创建时间
	private String zt;//状态（Y 启用， N 不启用）
	private ProcessVo processVo;

	public LcVo(){}

	public LcVo(Lcb lcb){
		this.id = lcb.getId();
		this.lcName = lcb.getLcName();
		this.lclb = lcb.getLclb();
		this.description = lcb.getDescription();
		this.cjz_id = lcb.getCjz().getId();
		this.cjsj = DateUtil.format(lcb.getCjsj(), DateUtil.fullFormat);
		this.zt = lcb.getZt();
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getCjz_id() {
		return cjz_id;
	}
	public void setCjz_id(String cjz_id) {
		this.cjz_id = cjz_id;
	}
	public String getCjsj() {
		return cjsj;
	}
	public void setCjsj(String cjsj) {
		this.cjsj = cjsj;
	}
	public String getZt() {
		return zt;
	}
	public void setZt(String zt) {
		this.zt = zt;
	}

	public ProcessVo getProcessVo() {
		return processVo;
	}

	public void setProcessVo(ProcessVo processVo) {
		this.processVo = processVo;
	}

	public static LcVo convert(Lcb lcb){
		LcVo lcVo = new LcVo();
		BeanUtils.copyProperties(lcb, lcVo);
		return lcVo;
	}

	public static List<LcVo> convert(List<Lcb> lcbList){
		List<LcVo> lcVoList = new ArrayList<>();
		if(!CollectionUtils.isEmpty(lcbList)){
			for(Lcb lcb : lcbList){
				LcVo lcVo = convert(lcb);
				lcVoList.add(lcVo);
			}
		}
		return lcVoList;
	}
	
}
