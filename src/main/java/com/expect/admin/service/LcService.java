package com.expect.admin.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.expect.admin.data.dao.UserRepository;
import com.expect.admin.data.dataobject.User;
import com.expect.admin.service.vo.LcVo;
import com.expect.admin.service.vo.UserVo;
import com.expect.admin.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expect.admin.data.dao.LcbRepository;
import com.expect.admin.data.dao.LcjdbRepository;
import com.expect.admin.data.dao.LcjdgxbRepository;
import com.expect.admin.data.dataobject.Lcb;
import com.expect.admin.data.dataobject.Lcjdb;
import com.expect.admin.data.dataobject.Lcjdgxb;
import com.expect.admin.exception.BaseAppException;
import com.expect.admin.utils.StringUtil;

@Service
public class LcService {

	@Autowired
	private LcbRepository lcbRepository;
	@Autowired
	private LcjdbRepository lcjdbRepository;
	@Autowired
	private LcjdgxbRepository lcjdgxbRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;


	/**
	 * 保存流程的相关信息
	 * @param lcVo
	 * @return
	 */
	public String addLcb(LcVo lcVo, String lcfqRole){
		lcVo.setCjsj(DateUtil.format(new Date(), DateUtil.fullFormat));
		//同一个类型的流程发起人角色只能有一个，没有时新建默认为"Y"，有的话设为"N"
		List<Lcb> roleLcb = lcbRepository.findAllByLclbAndLcfqRoleAndZt(lcVo.getLclb(),lcfqRole,"Y");
		if(roleLcb.size() == 0){
			lcVo.setZt("Y");
		}else{
			lcVo.setZt("N");
		}
		Lcb lcb = new Lcb(lcVo);
		lcb.setLcfqRole(lcfqRole);
		//设置流程创建者
		UserVo userVo = userService.getLoginUser();
		User user = userRepository.findOne(userVo.getId());
		lcb.setCjz(user);
		lcb = lcbRepository.save(lcb);
		return lcb.getId();
	}
	
	public String getDefaultLc(String category) {
		Lcb lcb = lcbRepository.findOne(category);
		return lcb == null ? "" : lcb.getId();
	}
	
	/**
	 * 获取某流程的起始状态
	 * @param lcbs
	 * @return
	 */
	public String getStartCondition(String lcbs) {
		return getCurLcjd("S", lcbs).getJsjd();
	}
	
	/**
	 * 判断某流程的当前状态是不是结束状态
	 * @param lcCategory 流程标识
	 * @param curCondition 当前状态标识
	 * @return true 是结束状态<br>
	 * flase 不是结束状态
	 */
	public boolean isEndConditon(String lcCategory, String curCondition) {
		return StringUtil.equals(
				getCurLcjd(curCondition, lcCategory).getJsjd(), "Y");
	}
	
	/**
	 * 获取当前状态在流程上的下一个状态的状态码
	 * 如果下一个状态唯一就直接返回
	 * 如果下一个状态不唯一就将后面多个同时进行的状态组合成“middle_状态1_状态2”来表示一个中间态
	 * @param lcCategory流程类别
	 * @param currentCondition 现在的状态
	 * @return
	 * @throws BaseAppException 
	 */
	public String getNextCondition(String lcId, String currentCondition) {
		Set<Lcjdgxb> curLcjdSet = getAllCurLcjd(lcId, currentCondition);
		return constructNextCondition(curLcjdSet);
	}
	
	/**
	 * 获取文件退回是应该返回到的状态码
	 * @param lcbs流程标识
	 * @param currentCondition当前状态
	 * @return
	 */
	public String getThCondition(String lcbs, String currentCondition) {
		Set<Lcjdgxb> curLcjdSet = getAllCurLcjd(lcbs, currentCondition);
		return constructThCondition(curLcjdSet) ;
	}
	
	
	
	private Set<Lcjdgxb> getAllCurLcjd(String lcId, String currentCondition) {
		if(StringUtil.isEmpty(currentCondition) || StringUtil.isEmpty(lcId)) throw new BaseAppException();
//		String lcId = getDefaultLc(lcCategory);
		Set<Lcjdgxb> curLcjdSet = new HashSet<Lcjdgxb>();
		if(currentCondition.startsWith("middle")){
			String[] curConditons = currentCondition.split("_");
			Set<String> curConditionSet = new HashSet<String>();
			for (int i = 1; i < curConditons.length; i++) {
				curConditionSet.add(curConditons[i]);
			}
			if(curConditionSet.size() == 1) {
				currentCondition = curConditionSet.iterator().next();
			}else {
				for (String curCon : curConditionSet) {
					curLcjdSet.add(getCurLcjd(curCon, lcId));
				}
			}
		}else {
			curLcjdSet.add(getCurLcjd(currentCondition, lcId));
		}
		return curLcjdSet;
	}

	/**
	 * 构造文件的下一个状态码
	 * @param curLcjdSet
	 * @return
	 */
	private String constructNextCondition(Set<Lcjdgxb> curLcjdSet) {
		if(curLcjdSet.size() > 1) {
			StringBuilder sb = new StringBuilder("middle_");
			for (Lcjdgxb curLcjd : curLcjdSet) {
				if(StringUtil.equals(curLcjd.getSftb(), "Y")) continue;
				sb.append(curLcjd.getJsjd()).append("_");
			}
			return sb.substring(0, sb.length() - 1).toString();
		}else return curLcjdSet.iterator().next().getJsjd();
	}

	/**
	 * 构造文件退回时下一个状态码
	 * @param curLcjdSet
	 * @return
	 */
	private String constructThCondition(Set<Lcjdgxb> curLcjdSet) {
		if(curLcjdSet.size() > 1) {
			StringBuilder sb = new StringBuilder("middle_");
			for (Lcjdgxb curLcjd : curLcjdSet) {
				if(StringUtil.isBlank(curLcjd.getThjd())) continue;
				sb.append(curLcjd.getThjd()).append("_");
			}
			return sb.substring(0, sb.length() - 1).toString();
		}else return curLcjdSet.iterator().next().getThjd();
	}
	/**
	 * 获取当前所处的节点
	 * @param currentCondition 文件所处的状态
	 * @param lcb 所使用的流程
	 * @return
	 */
	private Lcjdgxb getCurLcjd(String currentCondition, String lcbId) {
		Lcjdgxb curlcjd = lcjdgxbRepository.findByLcbsAndKsjd(lcbId, currentCondition);
		if(curlcjd ==  null) throw new BaseAppException("未找到当前的状态节点");
		return curlcjd;
	}
	
	/**
	 * @param lcCategory 流程类别
	 * @param jdCategory 节点类别
	 * @return
	 */
	public String getJd(String lcCategory, String jdCategory) {
		Lcb lcb = lcbRepository.findOne(lcCategory);
		if(lcb == null) return null;
		Lcjdb lcjdb = lcjdbRepository.findBySslcAndCategory(lcb.getId(), jdCategory);
		if(lcjdb == null) return null;
		return lcjdb.getId();
	}

	public Lcjdgxb getMaxLcId(){
		return lcjdgxbRepository.fingMaxLcId();
	}

}
