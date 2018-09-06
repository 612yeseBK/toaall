package com.expect.admin.service;

import java.util.ArrayList;
import java.util.List;

import com.expect.admin.data.dao.LcbRepository;
import com.expect.admin.data.dao.LcjdbRepository;
import com.expect.admin.data.dataobject.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expect.admin.data.dao.RoleJdgxbGxbRepository;
import com.expect.admin.data.dao.UserRepository;
import com.expect.admin.service.vo.RoleJdgxbGxbVo;
import com.expect.admin.service.vo.UserVo;

@Service
public class RoleJdgxbGxbService {
	
	@Autowired
	private RoleJdgxbGxbRepository roleJdgxbGxbRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private LcbRepository lcbRepository;
	@Autowired
	private LcjdbRepository lcjdbRepository;
	
	public RoleJdgxbGxbVo getByFunctionId(String roleId){
		return new RoleJdgxbGxbVo(
				roleJdgxbGxbRepository.findByRoleId(roleId));
	}
	
	/**
	 * 获取用户可以审核的文件的状态
	 * @param bz
	 * @param wjzl
	 * @return
	 */
	public List<RoleJdgxbGxbVo> getWjzt(String bz, String wjzl){
		UserVo userVo = userService.getLoginUser();
		User user = userRepository.findOne(userVo.getId());
//		User user = userRepository.findOne("2c913b71590fcb3201590fd15ada0007");
		if(user.getRoles() == null || user.getRoles().size() == 0) return null;
		List<String> roleIds = new ArrayList<>(user.getRoles().size());
		for (Role role : user.getRoles()) {
			roleIds.add(role.getId());
        }
		List<RoleJdgxbGxb> roleJdgxbGxbList = roleJdgxbGxbRepository.findByBzAndWjzlAndRoleIdIn(bz, wjzl, roleIds);//有错
        if(roleJdgxbGxbList == null) return null;
		List<RoleJdgxbGxbVo> list = new ArrayList<>();
		for (RoleJdgxbGxb roleJdgxbGxb:roleJdgxbGxbList){
			RoleJdgxbGxbVo roleJdgxbGxbVo = new RoleJdgxbGxbVo(roleJdgxbGxb);
			list.add(roleJdgxbGxbVo);
		}
		return list;
	}

	/**
	 * 根据所属流程分类以及启用状态找到相关的流程表，再根据流程表id找到所有节点
	 * 每个节点都与角色ID绑定，通过登录用户的角色筛选出自身所有的rolejdgxb
	 * 该rolejdgxb就是用户可以审核的文件的状态
	 * @param wjzl
	 * @return
	 */
	public List<RoleJdgxbGxbVo> getCondition(String wjzl){
		List<RoleJdgxbGxbVo> list = new ArrayList<>();

		//确定用户的所有相关角色id
		UserVo userVo = userService.getLoginUser();
		User user = userRepository.findOne(userVo.getId());
		if(user.getRoles() == null || user.getRoles().size() == 0) return null;
		List<String> roleIds = new ArrayList<>(user.getRoles().size());
		for (Role role : user.getRoles()) {
			roleIds.add(role.getId());
		}

		List<Lcb> lcbs = lcbRepository.findAllByLclbAndZt(wjzl, "Y");
		if(lcbs == null) return null;
		for(Lcb lcb : lcbs){
			String lcid = lcb.getId();
			List<Lcjdb> lcjdbs = lcjdbRepository.findBySslc(lcid);
			for(Lcjdb lcjdb : lcjdbs){
				String lcjdid = lcjdb.getId();
				RoleJdgxbGxb roleJdgxbGxb = roleJdgxbGxbRepository.findByJdIdAndAndRoleIdIn(lcjdid, roleIds);
				if(roleJdgxbGxb == null)
					continue;
				else{
					RoleJdgxbGxbVo roleJdgxbGxbVo = new RoleJdgxbGxbVo(roleJdgxbGxb);
					list.add(roleJdgxbGxbVo);
				}
			}

		}
		return list;
	}
}
