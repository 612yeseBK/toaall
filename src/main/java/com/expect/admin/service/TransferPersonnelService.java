package com.expect.admin.service;

import com.expect.admin.data.dao.AttachmentRepository;
import com.expect.admin.data.dao.TransPerRecordRepository;
import com.expect.admin.data.dao.TransferPersonnelRepository;
import com.expect.admin.data.dao.WorkFlowRepository;
import com.expect.admin.data.dataobject.*;
import com.expect.admin.data.pojo.TransPerson.*;
import com.expect.admin.service.vo.TransPerRecordVo;
import com.expect.admin.service.vo.TransferPersonnelVo;
import com.expect.admin.utils.DateUtil;
import com.expect.admin.utils.JsonUtil;
import com.expect.admin.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * description:人员借用业务处理
 * Created by gaoyw on 2018/5/6.
 */
@Service
public class TransferPersonnelService {
    private final Logger log = LoggerFactory.getLogger(TransferPersonnelService.class);
    @Autowired
    TransferPersonnelRepository tpRepository;
    @Autowired
    TransPerRecordRepository tprRepository;
    @Autowired
    TransPerRecordService tprService;
    @Autowired
    UserService userService;
    @Autowired
    WorkFlowService wfService;
    @Autowired
    WorkFlowRepository wfRepository;
    @Autowired
    AttachmentRepository attachmentRepository;
    @Autowired
    AttachmentService attachmentService;
    @Autowired
    TransferPersonnelService tpService;





    /**
     * 根据id值获取人员借用表的对象
     * @param id
     * @return
     */
    @Transactional
    public TransferPersonnel findById(String id){
        return tpRepository.findById(id);
    }

    @Transactional
    public TransperDetail getDetailFromTp(TransferPersonnel tp){
        TransperDetail tpDetail = new TransperDetail();
        tpDetail.setApplyTime(tp.getApplyTime());
        tpDetail.setBeginTime(tp.getBeginTime());
        tpDetail.setEndTime(tp.getEndTime());
        tpDetail.setId(tp.getId());
        tpDetail.setJob(tp.getJob());
        tpDetail.setNumber(tp.getNumber());
        tpDetail.setStaffNum(tp.getStaffNum());
        tpDetail.setToDepartment(tp.getToDepartment());
        tpDetail.setReason(tp.getReason());
        tpDetail.setAttachmentVos(attachmentService.getAttachmentVosBydto(tp.getAttachments()));
        System.out.println(JsonUtil.getInstance().toJson(tp.getAttachments()));
        List<Front_TransRec> front_transRecs = Front_TransRec.convert(tp.getTransPerRecords());
        tpDetail.setFront_transRecs(front_transRecs);
        return tpDetail;
    }

    /**
     * 新增一个人员借调申请，保存或新增
     * @param wkfid 流程id
     */
    @Transactional
    public void add(AddForm addForm, String wkfid, String[] ids,String bczl){
        TransferPersonnel transferPersonnel;
        if (StringUtil.isEmpty(addForm.getId())){
            transferPersonnel = new TransferPersonnel();
        } else{
            transferPersonnel = tpRepository.findOne(addForm.getId());
        }
        transferPersonnel.setApplicant(userService.getLogUsr());
        transferPersonnel.setReason(addForm.getReason());
        transferPersonnel.setToDepartment(addForm.getToDepartment());
        transferPersonnel.setJob(addForm.getJobKind());
        transferPersonnel.setNumber(Integer.parseInt(addForm.getNumber()));
        transferPersonnel.setBeginTime(addForm.getStartDate());
        transferPersonnel.setEndTime(addForm.getEndDate());
        transferPersonnel.setStaffNum(addForm.getStaffNum());
        Set<Attachment> attachments = new HashSet<>(attachmentRepository.findByIdIn(ids));
        transferPersonnel.setAttachments(attachments);
        transferPersonnel.setApplyTime(DateUtil.format(new Date(),DateUtil.fullFormat));
        if (bczl.equals("bc")) {
            //保存一定是新增的保存
            transferPersonnel.setWorkFlow(wfRepository.findOne(wkfid));
            WFPoint beginPoint = wfService.getBeginPoint(wkfid);
            transferPersonnel.setState(TransferPersonnel.NOTCOM);
            transferPersonnel.setNxtPoint(beginPoint);
            transferPersonnel = tpRepository.save(transferPersonnel);
            tprService.addOne("保存申请", "保存", beginPoint, transferPersonnel, userService.getLogUsr());
        } else {
            //tj有两种，已经保存的提交，和新增的提交
            WFPoint beginPoint;
            if (!StringUtil.isEmpty(wkfid)) {
                // 新增
                transferPersonnel.setWorkFlow(wfRepository.findOne(wkfid));
                beginPoint = wfService.getBeginPoint(wkfid);
            } else{
                beginPoint = transferPersonnel.getWorkFlow().getBeginPoint();
            }
            transferPersonnel.setState(TransferPersonnel.WAITING);
            transferPersonnel.setNxtPoint(beginPoint.getNxtPoint());
            transferPersonnel = tpRepository.save(transferPersonnel);
            tprService.addOne("提交已保存", "新增", beginPoint, transferPersonnel, userService.getLogUsr());
        }
    }


    /**
     * 提交已经保存的

     */
    @Transactional
    public void commitSaved(String id){
        TransferPersonnel tp = tpRepository.findOne(id);
        WFPoint beginPoint = tp.getWorkFlow().getBeginPoint();
        tp.setNxtPoint(beginPoint.getNxtPoint());
        tp.setState(TransferPersonnel.WAITING);
        tp = tpRepository.save(tp);
        tprService.addOne("提交已保存","新增",beginPoint, tp, userService.getLogUsr());
    }

    /**
     * 删除未提交的
     * @param id
     */
    @Transactional
    public void deleteWtj(String id){
        TransferPersonnel transferPersonnel = tpRepository.findOne(id);
        Set<TransPerRecord> transPerRecords = transferPersonnel.getTransPerRecords();
        for (TransPerRecord transPerRecord : transPerRecords){
            tprRepository.delete(transPerRecord);
        }
        transferPersonnel.setAttachments(null);
        transferPersonnel.setApplicant(null);
        transferPersonnel = tpRepository.save(transferPersonnel);
        tpRepository.delete(transferPersonnel);
    }

    /**
     * 根据用户以及对表单状态的要求，获取各种状态的表单
     * @param user
     * @param states
     * @return
     */
    public List<TransferPersonnel> getTransPerByState(User user,String[] states){
        List<TransferPersonnel> allLists = new ArrayList<>();
        List<TransferPersonnel> tps = tpRepository.findAll();
        for (TransferPersonnel tp: tps){
            if (tp.getApplicant().equals(user)){
                allLists.add(tp);
            }
        }
//        List<TransferPersonnel> allLists = new ArrayList(user.getTransferPersonnels());
        List<TransferPersonnel> stateLists = new ArrayList<>();
        int length = states.length;
        int i;
        for (TransferPersonnel tfp : allLists){
            for (i = 0;i < length;i ++) {
                if (tfp.getState().equals(states[i])) {
                    stateLists.add(tfp);
                }
            }
        }
        return stateLists;
    }


    public List<TransferPersonnelVo> getTransPerVoByState(User user,String[] states){
        List<TransferPersonnel> stateLists = getTransPerByState(user,states);
        return getTpVoFromList(stateLists);
    }

    /**
     * 获取申请人表格的记录，依据触发按钮和请求的人员返回相应的数据
     * @param user
     * @param state
     * @return
     */
    @Transactional
    public List<TransAplyRecds> getApplyRecs(User user,String state){
        String[] states;
        if (StringUtil.isEmpty(state)) {
            states = new String[]{TransferPersonnel.NOTCOM};
        } else if (state.equals("wtj")){
            states = new String[]{TransferPersonnel.NOTCOM};
        } else if (state.equals("dsp")){
            states = new String[]{TransferPersonnel.WAITING};
        } else {
            states = new String[]{TransferPersonnel.PASSED,TransferPersonnel.REJECTED};
        }
        List<TransferPersonnel> transferPersonnels = tpService.getTransPerByState(user,states);
        List<TransAplyRecds> transAplyRecds = new ArrayList<>();
        for (TransferPersonnel tp: transferPersonnels){
            TransAplyRecds tar = new TransAplyRecds();
            tar.setApplyDate(tp.getApplyTime());
            tar.setId(tp.getId());
            tar.setJobKind(tp.getJob());
            if (!tp.getNxtPoint().getName().equals(WFPoint.ENDPOINT)){
                tar.setState (tp.getNxtPoint().getName());
            } else if(tp.getState().equals(TransferPersonnel.PASSED)){
                tar.setState( "已通过");
            } else {
                tar.setState( "未通过");
            }
            transAplyRecds.add(tar);
        }
        return transAplyRecds;
    }

    /**
     * 根据登录用户，获取该用户所有需要审批的表单，所有处于同一节点的用户会看到同样的类容
     * @param user
     * @return
     */
    @Transactional
    public List<TransferPersonnelVo> getCheckForms(User user){
        List<TransferPersonnel> tfps = tpRepository.findAll();
        List<TransferPersonnel> tfpsNeed = new ArrayList<>();
        Set<User> users;
        for (TransferPersonnel tfp : tfps){
            if (!tfp.getNxtPoint().getName().equals(WFPoint.ENDPOINT)) {
                users = tfp.getNxtPoint().getRole().getUsers();
                for (User u : users) {
                    if (u.equals(user)) {
                        tfpsNeed.add(tfp);
                    }
                }
            }
        }
        return getTpVoFromList(tfpsNeed);
    }

    /**
     * 根据登录用户，获取该用户所有的审批记录
     * 调整为获取同一节点的用户的所有审批记录
     * @param user
     * @return
     */
    @Transactional
    public List<TransferPersonnelVo> getCheckedForms(User user){
        List<TransPerRecord> tprs = tprRepository.findAll();
        List<TransferPersonnel> tps = new ArrayList<>();
        for (TransPerRecord tpr : tprs){
            // 该记录需要时审批记录
            if (!(tpr.getMessage().equals("新增")||tpr.getMessage().equals("保存"))) {
                Set<User> users = tpr.getWfPoint().getRole().getUsers();
                for (User user1 : users) {
                    // 该记录所处的节点是包含该用户
                    if (user.equals(user1)) {
                        tps.add(tpr.getTransferPersonnel());
                    }
                }
            }
        }
//        //调整前的获取
//        List<TransPerRecord> tprs = tprRepository.findByUserId(usrid);
//        List<TransferPersonnel> tps = new ArrayList<>();
//        for (TransPerRecord tpr : tprs){
//            //根据记录的信息判断这个记录是提交保存记录，还是审批记录
//            if (!(tpr.getMessage().equals("新增")||tpr.getMessage().equals("保存"))) {
//                tps.add(tpr.getTransferPersonnel());
//            }
//        }
        return getTpVoFromList(tps);
    }


    /**
     * 获取某个用户对某个申请的审批详情
     * @param usrid
     * @param tpid
     * @return
     */
    public TransPerRecordVo getRcdBytpAndUsr(String usrid, String tpid){
        TransPerRecord tpr = tprRepository.findByUserAndTransferPersonnel(usrid,tpid);
        return TransPerRecordVo.convert(tpr);
    }

    /**
     * 根据申请表单的id获得该表单当前所有的审核记录
     * @param id
     * @return
     */
    public List<TransPerRecordVo> getRecdsByTransId(String id){
        TransferPersonnel tp = tpRepository.findOne(id);
        List<TransPerRecord> tprs = new ArrayList<>(tp.getTransPerRecords());
        Collections.sort(tprs, new Comparator<TransPerRecord>() {
            @Override
            public int compare(TransPerRecord o1, TransPerRecord o2) {
                return o1.getClsj().compareTo(o2.getClsj());
            }
        });
        return getTprVoFromList(tprs);
    }



    /**
     * 某个审批人审核的结果，存入表格
     * @param id
     * @param message
     * @param cljg
     */
    @Transactional
    public void checkApply(String id,String message,String cljg){
        TransferPersonnel tp = tpRepository.findOne(id);
        WFPoint wfp = tp.getNxtPoint(); //当前流程
        tprService.addOne(message,cljg,wfp,tp,userService.getLogUsr());
        if (cljg.equals("不通过")){
            tp.setState(TransferPersonnel.REJECTED);
            tp.setNxtPoint(tp.getWorkFlow().getBeginPoint().getPrePoint());
        } else if (cljg.equals("通过")){
            tp.setNxtPoint(wfp.getNxtPoint());
            if (wfp.getNxtPoint().getName().equals(WFPoint.ENDPOINT)){
                tp.setState(TransferPersonnel.PASSED);
            }
        }

        tpRepository.save(tp);
    }


    /**
     * 将TransferPersonnel对象list转变成TransferPersonnelVo对象list
     * @param lists
     * @return
     */
    public List<TransferPersonnelVo> getTpVoFromList(List<TransferPersonnel> lists){
        List<TransferPersonnelVo> tfpVos = new ArrayList<>();
        for (TransferPersonnel tp : lists){
            tfpVos.add(TransferPersonnelVo.convert(tp));
        }
        return tfpVos;
    }

    /**
     * 将TransPerRecord对象list转变成TransPerRecordVo对象list
     * @param tprs
     * @return
     */
    public List<TransPerRecordVo> getTprVoFromList(List<TransPerRecord> tprs){
        List<TransPerRecordVo> tprVos = new ArrayList<>();
        for (TransPerRecord tpr : tprs){
            tprVos.add(TransPerRecordVo.convert(tpr));
        }
        return tprVos;
    }

    public TransPrint getTransPrintFromTransP(TransferPersonnel tp){
        List<TransPerRecordVo> transPerRecordVos = TransPerRecordVo.convert(tp.getTransPerRecords());
        String records="";
        int i = 0;
        for (TransPerRecordVo tprVo:transPerRecordVos){
            if (!tprVo.getCljg().equals("保存")) {
                //保存的记录不需要展示
                if (i==0){
                    records = records + "申请人: " +tprVo.getUserVo().getUsername()+"("+ tprVo.getUserVo().getFullName()+")" + " "
                            + "   申请时间：" + DateUtil.format(tprVo.getClsj(),DateUtil.zbFormat) + "<br/><br/>";
                } else {
                    records = records + "审核人: " + tprVo.getUserVo().getUsername()+"("+tprVo.getUserVo().getFullName() +")"+ " "
                            + "   处理时间：" + DateUtil.format(tprVo.getClsj(), DateUtil.zbFormat) + "<br/>";
                }
                if (!tprVo.getCljg().equals("新增")) {
                    //新增记录不需要有处理意见
                    records = records + "处理意见：" + (StringUtil.isEmpty(tprVo.getMessage()) ? "通过" : tprVo.getMessage())
                            + "<br/><br/>";
                }
                i++;
            }
        }

        User applicant = tp.getApplicant();
        TransPrint transPrint = new TransPrint();
        transPrint.setId(tp.getId());
        transPrint.setName(applicant.getFullName());
        transPrint.setCompany("江宁公交集团");
        transPrint.setJobKind(tp.getJob());
        transPrint.setNumber(Integer.toString(tp.getNumber()));
        transPrint.setSex(applicant.getSex());
        transPrint.setReason(tp.getReason());
        transPrint.setStaffNum(tp.getStaffNum());
        transPrint.setTimePeriod(tp.getBeginTime()+"-"+tp.getEndTime());
        transPrint.setRecords(records);
        transPrint.setToDepartment(tp.getToDepartment());
        transPrint.setUserJob(applicant.getUsername());
        return transPrint;
    }

    public TransPrint getTransPrintFromtpId(String id){
        return getTransPrintFromTransP(tpRepository.findOne(id));
    }
}
