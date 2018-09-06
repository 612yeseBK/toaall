package com.expect.admin.service;

import com.expect.admin.data.dao.*;
import com.expect.admin.data.dataobject.*;
import com.expect.admin.data.pojo.*;
import com.expect.admin.exception.BaseAppException;
import com.expect.admin.exception.NoKindWorkFlowException;
import com.expect.admin.service.convertor.FunctionConvertor;
import com.expect.admin.service.vo.FunctionVo;
import com.expect.admin.service.vo.WorkFlowVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * description:更新需求后的流程，不包括合同，收发文，会议
 * Created by gaoyw on 2018/5/6.
 */
@Service
public class WorkFlowService {
    private final Logger log = LoggerFactory.getLogger(WorkFlowService.class);
    @Autowired
    WorkFlowRepository workFlowRepository;
    @Autowired
    FunctionRepository functionRepository;
    @Autowired
    TransferPersonnelService tpService;
    @Autowired
    TransferPersonnelRepository tpRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    WFPointRepository wfPointRepository;
    @Autowired
    WFPointService wfPointService;

    /**
     * 根据流程类别和申请者姓名找到该申请者所有允许的申请，并返回
     * @param type
     * @param user
     * @return
     */
    public List<WorkFlow> findAplt(String type,User user){
        List<WorkFlow> aplt = new ArrayList<>();
        List<WorkFlow> wfs = workFlowRepository.findAllByType(type);
        for (WorkFlow wf : wfs){
            if (wf.getBeginPoint().getRole().getUsers().contains(user)){
                aplt.add(wf);
            }
        }
        return aplt;
    }

    /**
     * 获取所有的菜单
     */
    public List<IdName> getFunctions(String pareName) throws BaseAppException{
        List<Function> functions = functionRepository.findAll();
        Function pareFunc = new Function();
        boolean flag = false;
        for (Function function : functions){
            if (function.getName().equals(pareName)){
                pareFunc = function;
                flag = true;
                break;
            }
        }
        if (flag == false){
            throw new BaseAppException("没有找到名为: "+pareName+" 的父功能");
        }
        Set<Function> childFuncs = pareFunc.getChildFunctions();
        List<IdName> idNames = new ArrayList<>();
        IdName idName = new IdName();
        for (Function fuc : childFuncs){
            idName.setId(fuc.getId());
            idName.setName(fuc.getName());
            idNames.add(idName);
            idName = new IdName();
        }
        return idNames;
    }

    /**
     * 根据可以申请的流程获取返回前端的数据
     * @param wkfls
     * @return
     */
    public List<WorkFlowVo> getVoFromWfs(List<WorkFlow> wkfls){
        List<WorkFlowVo> wfvos = new ArrayList<>();
        for (WorkFlow wf : wkfls){
            wfvos.add(new WorkFlowVo(wf));
        }
        return wfvos;
    }


    /**
     * 根据流程id获取该流程的第一个节点
     * @param wkfid
     * @return
     */
    public WFPoint getBeginPoint(String wkfid){
        WorkFlow workFlow = findOne(wkfid);
        WFPoint wfPoint = workFlow.getBeginPoint();
        return wfPoint;
    }


//    public WorkFlow addWF(Addwf addwf){
//        WorkFlow workFlow = new WorkFlow();
//        workFlow.setName(addwf.getName());
//        workFlow.setType(addwf.getType());
//        workFlow.setDescription(addwf.getDescription());
//        workFlow = save(workFlow);
//        return workFlow;
//    }
    /**
     * 建立新的流程
     */
    public WorkFlow addWF(WfDetail wfDetail){
        WorkFlow workFlow = new WorkFlow();
        workFlow.setName(wfDetail.getName());
        workFlow.setType(wfDetail.getType());
        workFlow.setDescription(wfDetail.getDescription());
        workFlow = save(workFlow);
        return workFlow;
    }


    /**
     * 对流程的名称和描述进行修改
     * @param id
     * @param name
     * @param descpt
     */
    @Transactional
    public void updWfNamAndDes(String id,String name,String descpt){
        WorkFlow workFlow = workFlowRepository.findOne(id);
        workFlow.setName(name);
        workFlow.setDescription(descpt);
        workFlowRepository.save(workFlow);
    }

    /**
     * 获取所有的流程简略信息
     * @return
     */
    public List<WfInfo> getAllWfInfo() throws NoKindWorkFlowException{
        List<WorkFlow> workFlows = findAll();
        List<WfInfo> wfInfos = new ArrayList<>();
        WfInfo wfInfo = new WfInfo();
        for (WorkFlow wf : workFlows){
            wfInfo.setId(wf.getId());
            wfInfo.setName(wf.getName());
            wfInfo.setTypeName(WorkFlow.map.get(wf.getType()));
            wfInfo.setDescription(wf.getDescription());
            wfInfo.setCandele(canDelete(wf));
            wfInfos.add(wfInfo);
            wfInfo = new WfInfo();
        }
        return wfInfos;
    }

    /**
     * 根据id获取该流程的详细信息（包括节点）
     * @param id
     * @return
     */
    public WfDetail getWfInfoById(String id){
        WfDetail wfDetail = new WfDetail();
        WorkFlow workFlow = findOne(id);
        wfDetail.setName(workFlow.getName());
        wfDetail.setDescription(workFlow.getDescription());
        wfDetail.setId(workFlow.getId());
        wfDetail.setType(workFlow.getType());
        List<WfpInfo> wfpIs = new ArrayList<>();
        WFPoint wfPoint = workFlow.getBeginPoint();
        while (!wfPoint.getName().equals(WFPoint.ENDPOINT)){
            List<IdName> idNames = new ArrayList<>();
            WfpInfo wfpInfo = new WfpInfo();
            wfpInfo.setId(wfPoint.getId());
            wfpInfo.setName(wfPoint.getName());
            //这个角色是为了这个节点新建的，里面我们只能添加一个funcition，所以只需要取一个就可以了
            Set<Function> functions = wfPoint.getRole().getFunctions();
            List<String> funcids = new ArrayList<>();
            for (Function function:functions){
                funcids.add(function.getId());
            }
            String[] funcids2 = new String[funcids.size()];
            wfpInfo.setFuncId(funcids.toArray(funcids2));
            for (User user : wfPoint.getRole().getUsers()){
                IdName idName = new IdName();
                idName.setId(user.getId());
                idName.setName(user.getFullName()+"("+user.getUsername()+")");
                idNames.add(idName);
            }
            wfpInfo.setUsersInfos(idNames);
            wfpIs.add(wfpInfo);
            wfPoint = wfPoint.getNxtPoint();
        }
        wfDetail.setWfpInfos(wfpIs);
        return wfDetail;
    }



    @Transactional
    public WorkFlow findOne(String id){
        return workFlowRepository.findOne(id);
    }

    @Transactional
    public List<WorkFlow> findAll(){
        return workFlowRepository.findAll();
    }

    @Transactional
    public WorkFlow save(WorkFlow workFlow){
        return workFlowRepository.save(workFlow);
    }

    /**
     * 根据id删除这个流程
     * 删除流程要考虑到存在一些已经在流程里面的申请审批
     * 这时候我们可以审查，如果有，就进行提示
     * 如果删除了，那么正在审批中的申请会全部作废，
     * 删除只是不让这个流程再被申请，流程依然存在，考虑到流程存在一个字段type，我这里直接将这个字段修改为delete
     * 这时候再考虑历史记录的问题，我给user表格加了一个属性，可以直接查找到用户的调用申请记录，通过申请记录可以直接找到审批记录
     * 所以，删除流程并不影响到我的查找，查找之后展示记录需要由申请记录找到流程，通过流程的type去判断这个申请是否已经弃用，弃用的话
     * 就不再加入原有的体系，可以额外新建一个新的表格，专门存放
     * @param id
     */
    @Transactional
    public void sorftDelete(String id){
        WorkFlow workFlow = workFlowRepository.findOne(id);
        String beforeType = workFlow.getType();
        workFlow.setType("delete_"+beforeType);
        workFlowRepository.save(workFlow);
    }

    @Transactional
    public void reuse(String id){
        WorkFlow workFlow = workFlowRepository.findOne(id);
        String beforeType = workFlow.getType();
        int i = beforeType.indexOf("_");
        String recover = beforeType.substring(i+1,beforeType.length());
        System.out.println("============="+recover);
        workFlow.setType(recover);
        workFlowRepository.save(workFlow);
    }


    public boolean canDelete(WorkFlow workFlow) throws NoKindWorkFlowException{
        String temp_type = workFlow.getType();
        String real_type;
        int i = temp_type.indexOf("delete");
        real_type = (i==-1) ? temp_type:temp_type.substring(i+7,temp_type.length());
        if (real_type.equals(WorkFlow.TRANS_PERSON)){
            List<TransferPersonnel> tps = tpRepository.findAll();
            for (TransferPersonnel tp: tps){
                if (tp.getWorkFlow().getId().equals(workFlow.getId())){
                    return false;
                }
            }
            return true;
        }
        // 如果有新的类别需要在这里添加新的判断
        else{
            throw new NoKindWorkFlowException("请在WorkFlowService文件里面添加这个工厂方法");
        }
    }



    /**
     * 真正意义上删除流程
     * 流程必须要是那种没有人使用过的
     * 先删除节点的人员和角色，然后删除节点
     * @param id
     */
    public boolean realDelete (String id) throws NoKindWorkFlowException {
        WorkFlow workFlow = workFlowRepository.findOne(id);
        // 如果可以删则删除
        if (canDelete(workFlow)){
            return UnchekrealDelete(workFlow);
        } else{
            // 不可以删除返回false
            return false;
        }
    }

    /**
     * 这是不加以审查检验的删除方法
     * @param id
     * @return
     * @throws NoKindWorkFlowException
     */
    public boolean UnchekrealDelete (String id) throws NoKindWorkFlowException {
        WorkFlow workFlow = workFlowRepository.findOne(id);
        return UnchekrealDelete(workFlow);
    }

    public boolean UnchekrealDelete (WorkFlow workFlow) throws NoKindWorkFlowException {
        WFPoint wfPoint = workFlow.getBeginPoint();
        while (!wfPoint.getName().equals(WFPoint.ENDPOINT)){
            Role role = wfPoint.getRole();
            Set<User> users = role.getUsers();
            for (User user : users){
                user.getRoles().remove(roleRepository.findOne(role.getId()));
                userRepository.save(user);
            }
            wfPoint.setRole(null);
            wfPointRepository.save(wfPoint);
            role.setFunctions(null);
            roleRepository.save(role);
            roleRepository.delete(role);
            wfPoint = wfPoint.getNxtPoint();
        }
        wfPointService.deleteAllPoints(workFlow);
        workFlowRepository.delete(workFlow);
        return true;
    }



}
