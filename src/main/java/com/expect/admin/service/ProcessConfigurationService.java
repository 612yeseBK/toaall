package com.expect.admin.service;

import com.expect.admin.data.dao.LcbRepository;
import com.expect.admin.data.dao.LcjdbRepository;
import com.expect.admin.data.dao.LcjdgxbRepository;
import com.expect.admin.data.dao.RoleJdgxbGxbRepository;
import com.expect.admin.data.dataobject.*;
import com.expect.admin.service.vo.LcVo;
import com.expect.admin.service.vo.ProcessDataVo;
import com.expect.admin.service.vo.RoleVo;
import com.expect.admin.service.vo.UserVo;
import com.expect.admin.service.vo.component.ResultVo;
import com.expect.admin.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

/**
 * Created by qifeng on 17/11/20.
 */
@Service
public class ProcessConfigurationService {

    @Autowired
    LcService lcService;
    @Autowired
    LcjdbRepository lcjdbRepository;
    @Autowired
    LcjdgxbRepository lcjdgxbRepository;
    @Autowired
    RoleService roleService;
    @Autowired
    RoleJdgxbGxbRepository roleJdgxbGxbRepository;

    /**
     * 通过lcvo以及节点表格信息，分别进行数据库中三个表lcb,lcjdb,lcjdgxb的保存
     * @param lcVo
     * @param list
     */
    @Transactional
    public ResultVo save(LcVo lcVo, List<ProcessDataVo> list){
        ResultVo resultVo = new ResultVo();
        //检查节点与角色的绑定是否合法
        resultVo = CommonCheck(resultVo, lcVo, list);
        if(lcVo.getLclb().equals("ht") && resultVo.getMessage() == null){
            resultVo = checkContract(resultVo, lcVo, list);
        }
        if(resultVo.getMessage() != null){
            return resultVo;
        }else{
            //保存lcb信息
            String lcfqRole = list.get(0).getRoleId();
            String lcid = lcService.addLcb(lcVo, lcfqRole);

            //保存lcjdb和lcjdgxb
            String startId = "S";
            for(int i = 0; i < list.size(); i++){

                ProcessDataVo processDataVo = list.get(i);
                Lcjdb lcjdb = lcjdbRepository.save(new Lcjdb(processDataVo.getNodeName(), lcid));
                String lcjdId = lcjdb.getId();
                buildNodeRole(lcjdId, processDataVo, lcVo.getLclb());

                Lcjdgxb lcjdgxb  = new Lcjdgxb(startId,lcjdId,null,"Y","N",lcid);
                startId = lcjdId;
                Lcjdgxb lcjdgxb1 = lcjdgxbRepository.save(lcjdgxb);

            }
            Lcjdgxb lcjdgxb = lcjdgxbRepository.save(new Lcjdgxb(startId,"Y",null,"Y","N",lcid));

            resultVo.setResult(true);
            resultVo.setMessage("流程信息配置成功！");

            return resultVo;
        }
    }


    public ResultVo CommonCheck(ResultVo resultVo, LcVo lcVo, List<ProcessDataVo> list){
        Set<String> set = new HashSet<>();
        for(ProcessDataVo processDataVo : list){
            Integer xh = processDataVo.getXh();
            String roleId = processDataVo.getRoleId();
            if(xh == 1){
                if(!isApply(roleId)){
                    resultVo.setMessage("第一个节点没有申请功能");
                    return resultVo;
                }
                set.add(processDataVo.getRoleId());
            }else{
                //判断除了第一个节点之外是否都具有审批的功能
                boolean isApprove = false;
                Set<Function> functions = roleService.getFunctionsByRoleId(roleId);
                for (Function function:functions){
                    if (function.getName() !=null && function.getName().indexOf("审批") != -1){
                        isApprove = true;
                    }
                }
                if(isApprove == false){
                    resultVo.setMessage("第"+(processDataVo.getXh())+"个节点没有审批功能");
                    return resultVo;
                }
                set.add(processDataVo.getRoleId());
            }
        }
        //判断是否有节点角色重复
        if (set.size() != list.size()) {
            resultVo.setMessage("节点角色重复");
            return resultVo;
        }
        return resultVo;
    }


    public ResultVo checkContract(ResultVo resultVo, LcVo lcVo, List<ProcessDataVo> list){
        //检查节点是否有资产管理部合同审核员和董事长角色
        boolean isZcglb =false;
        boolean isDsz = false;
        for(ProcessDataVo processDataVo : list){
            String roleId = processDataVo.getRoleId();
            RoleVo roleVo = roleService.getRoleById(roleId);
            if (roleVo.getName().equals("资产管理部合同审核员")){
                isZcglb = true;
            }
            if (roleVo.getName().equals("负责人")){
                isDsz = true;
            }
        }
        if (isZcglb ==false){
            resultVo.setMessage("节点未绑定资产管理部合同审核员");
            return resultVo;
        }
        if (isDsz ==false){
            resultVo.setMessage("节点未绑定负责人");
            return resultVo;
        }
        return resultVo;
    }

    public void buildNodeRole(String nodeId, ProcessDataVo processDataVo, String lclb){
        String bz;
        if(processDataVo.getXh() == 1){
            bz = "sq";
        }else{
            bz = "sp";
        }
        RoleJdgxbGxb roleJdgxbGxb  = new RoleJdgxbGxb(bz,nodeId, processDataVo.getRoleId(),lclb);
        roleJdgxbGxbRepository.save(roleJdgxbGxb);
    }


    //    检查第一个节点是否有申请功能
    public boolean isApply(String roleId){
        Set<Function> functions = roleService.getFunctionsByRoleId(roleId);
        for (Function function:functions){
            if (function.getName() !=null && function.getName().indexOf("申请") != -1){
                return true;
            }
        }
        return false;
    }

}
