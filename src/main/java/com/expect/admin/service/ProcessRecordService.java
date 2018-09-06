package com.expect.admin.service;

import com.expect.admin.data.dao.*;
import com.expect.admin.data.dataobject.*;
import com.expect.admin.service.vo.LcVo;
import com.expect.admin.service.vo.ProcessDetailVo;
import com.expect.admin.service.vo.ProcessVo;
import com.expect.admin.service.vo.component.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by qifeng on 17/12/8.
 */
@Service
public class ProcessRecordService {
    @Autowired
    LcjdgxbRepository lcjdgxbRepository;
    @Autowired
    LcjdbRepository lcjdbRepository;
    @Autowired
    RoleJdgxbGxbRepository roleJdgxbGxbRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    LcbRepository lcbRepository;
    @Autowired
    private TravelingService travelingService;
    @Autowired
    private ContractService contractService;

    public List<ProcessVo> getContractProcess(){
        List<ProcessVo> processVoList = new ArrayList<>();
        List<Lcjdgxb> lcjdgxbList = lcjdgxbRepository.findAll();
        Map<String,String> startId = new HashMap<>();
        for (Lcjdgxb lcjdgxb:lcjdgxbList){
            if (lcjdgxb.getKsjd() !=null && lcjdgxb.getKsjd().equals("S")){
                startId.put(lcjdgxb.getId(),lcjdgxb.getLcbs());
            }
        }
        for (Map.Entry<String,String> entry:startId.entrySet()){
            processVoList.add(getProcessByKsIdAndLcId(entry.getKey(),entry.getValue()));
        }

        return processVoList;
    }
    public ProcessVo getProcess(String lcid) {
        ProcessVo processVo = new ProcessVo();
        List<ProcessDetailVo> processDetailVos = new ArrayList<>();
        String startId = "S";
        int processXH = 1;
        while(true){
            String nodeId = lcjdgxbRepository.findByLcbsAndKsjd(lcid, startId).getJsjd();
            startId = nodeId;
            if(nodeId != null && !nodeId.equals("Y")){
                ProcessDetailVo processDetailVo = new ProcessDetailVo();
                processDetailVo.setProcessId(lcid);
                processDetailVo.setProcessXh(processXH++);
                String nodeName = lcjdbRepository.findOne(nodeId).getName();
                processDetailVo.setNodeId(nodeId);
                processDetailVo.setNodeName(nodeName);
                List<RoleJdgxbGxb> roleJdgxbGxbs = roleJdgxbGxbRepository.findByJdId(nodeId);
                List<String> roleIds = new ArrayList<>();
                List<String> roleNames = new ArrayList<>();
                for (RoleJdgxbGxb roleJdgxbGxb:roleJdgxbGxbs){
                    String roleId = roleJdgxbGxb.getRoleId();
                    String roleName = roleRepository.getOne(roleId).getName();
                    List users = getUsers(roleId);
                    StringBuilder sb = new StringBuilder();
                    for(int i = 0; i < users.size(); i++){
                        if(i == 0)
                            sb.append("(");
                        if(i < users.size() - 1){
                            sb.append(users.get(i) + " ");
                        }else{
                            sb.append(users.get(i) + ")");
                        }
                    }
                    roleName = roleName + sb;
                    roleIds.add(roleId);
                    roleNames.add(roleName);
                }
                processDetailVo.setRoleId(roleIds);
                processDetailVo.setRoleName(roleNames);
                processDetailVos.add(processDetailVo);

            }else{
                break;
            }

        }

        processVo.setProcessDetai(processDetailVos);
        return processVo;
    }
    public ProcessVo getProcessByKsIdAndLcId(String startId,String lcId){
        ProcessVo processVo = new ProcessVo();
        processVo.setProcessId(lcId);
        List<ProcessDetailVo> processDetailVos = new ArrayList<>();

        while(true){
            String nodeId = lcjdgxbRepository.findOne(startId).getJsjd();
            if (nodeId !=null && !nodeId.equals("Y")){
                ProcessDetailVo processDetailVo = new ProcessDetailVo();
                processDetailVo.setProcessId(lcId);
                String nodeName = lcjdbRepository.findOne(nodeId).getName();
                List<RoleJdgxbGxb> roleJdgxbGxbs = roleJdgxbGxbRepository.findByJdId(nodeId);
                processDetailVo.setNodeId(nodeId);
                processDetailVo.setNodeName(nodeName);

                List<String> roleNames = new ArrayList<>();
                for (RoleJdgxbGxb roleJdgxbGxb:roleJdgxbGxbs){
                    String roleId = roleJdgxbGxb.getRoleId();
                    String roleName = roleRepository.getOne(roleId).getName();
                    roleNames.add(roleName);
                }
                processDetailVo.setRoleName(roleNames);

                processDetailVos.add(processDetailVo);

            }
            else{
                break;
            }
            startId = lcjdgxbRepository.findByLcbsAndKsjd(lcId,nodeId).getId();

        }
        processVo.setProcessDetai(processDetailVos);
        return processVo;
    }

    public ResultVo deleteProcess(String processId){
        ResultVo resultVo = new ResultVo();
        List<Lcjdgxb> lcjdgxbList = lcjdgxbRepository.findByLcbsOrderByXssx(processId);
        if (processId.length() ==0 || lcjdgxbList.size() == 0){
            resultVo.setMessage("合同流程不存在");
        }
        for (Lcjdgxb lcjdgxb:lcjdgxbList){
           lcjdgxbRepository.delete(lcjdgxb);
        }
        resultVo.setResult(true);
        return resultVo;

    }

    public List<Lcb> getAllLcb(){
        List<Lcb> lcbList = lcbRepository.findAll();
        for(Lcb lcb : lcbList){
            if(lcb.getLclb().equals("ht"))
                lcb.setLclb("合同管理");
            else if(lcb.getLclb().equals("cc"))
                lcb.setLclb("出差管理");
        }
        return lcbList;
    }

    public LcVo getLcb(String id){
        Lcb lcb = lcbRepository.findById(id);
        if(lcb.getLclb().equals("ht"))
            lcb.setLclb("合同管理");
        else if(lcb.getLclb().equals("cc"))
            lcb.setLclb("出差管理");
        if(lcb.getZt().equals("Y"))
            lcb.setZt("使用中");
        else if(lcb.getZt().equals("N"))
            lcb.setZt("停用中");
        LcVo lcVo = new LcVo(lcb);
        ProcessVo processVo = getProcess(id);
        lcVo.setProcessVo(processVo);
        return lcVo;
    }

    public ArrayList<String> getUsers(String roleId){
        ArrayList<String> userList = new ArrayList<>();
        Role role = roleRepository.findById(roleId);
        Set<User> users = role.getUsers();
        if(users != null || users.isEmpty()){
            for(User user : users){
                String userName = user.getFullName();
                userList.add(userName);
            }
        }
        return userList;
    }

    public ResultVo closeLcb(String lcid){
        ResultVo resultVo = new ResultVo();
        Lcb lcb = lcbRepository.findById(lcid);
        if(lcb == null){
            resultVo.setMessage("流程信息不存在");
        }
        String lclb = lcb.getLclb();
        if(lclb.equals("cc")){
            travelingService.closeTraveling(lcid);
        }else if(lclb.equals("ht")){
            contractService.closeContract(lcid);
        }
        String zt = lcb.getZt();
        if(zt.equals("Y")){
            lcb.setZt("N");
        }
        lcbRepository.save(lcb);
        resultVo.setMessage("停用流程成功");
        resultVo.setResult(true);

        return resultVo;
    }

    public ResultVo openLcb(String lcid){
        ResultVo resultVo = new ResultVo();
        Lcb lcb = lcbRepository.findById(lcid);
        if(lcb == null){
            resultVo.setMessage("流程信息不存在");
            return resultVo;
        }
        String lcfqr = lcb.getLcfqRole();
        String lclb = lcb.getLclb();
        List<Lcb> lcbList = lcbRepository.findAllByLclbAndLcfqRoleAndZt(lclb, lcfqr, "Y");
        if(lcbList == null || lcbList.size() == 0){
            lcb.setZt("Y");
        }else{
            resultVo.setMessage("已有该相关流程被启用");
            return resultVo;
        }


        lcbRepository.save(lcb);
        resultVo.setMessage("启用流程成功");
        resultVo.setResult(true);

        return resultVo;
    }


}
