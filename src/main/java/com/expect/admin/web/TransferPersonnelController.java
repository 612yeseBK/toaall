package com.expect.admin.web;

import com.expect.admin.config.Settings;
import com.expect.admin.data.dataobject.*;
import com.expect.admin.data.pojo.TransPerson.AddForm;
import com.expect.admin.data.pojo.TransPerson.TransAplyRecds;
import com.expect.admin.data.pojo.TransPerson.TransPrint;
import com.expect.admin.data.pojo.TransPerson.TransperDetail;
import com.expect.admin.data.pojo.WfDetail;
import com.expect.admin.factory.WordXmlFactory;
import com.expect.admin.factory.impl.TransPerFactory;
import com.expect.admin.service.AttachmentService;
import com.expect.admin.service.TransferPersonnelService;
import com.expect.admin.service.UserService;
import com.expect.admin.service.WorkFlowService;
import com.expect.admin.service.vo.*;
import com.expect.admin.service.vo.component.FileResultVo;
import com.expect.admin.service.vo.component.ResultVo;
import com.expect.admin.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * description:
 * Created by gaoyw on 2018/5/14.
 */
@Controller
@RequestMapping("/admin/transper")
public class TransferPersonnelController {
    Logger logger = LoggerFactory.getLogger(TransferPersonnelController.class);
    private final String viewName = "admin/transPerson/";

    @Autowired
    WorkFlowService wfs;
    @Autowired
    UserService userService;
    @Autowired
    TransferPersonnelService tpService;
    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private Settings settings;


    @RequestMapping("/getAllowForms")
    @ResponseBody
    public ModelAndView getFormsHtml(){
        ModelAndView mv = new ModelAndView(viewName+"choose_transper");
        return mv;
    }

    /**
     * 根据登录用户获取所有的他可以申请的人员借调流程
     */
    @RequestMapping("/getFormsInfo")
    @ResponseBody
    public List<WorkFlowVo> getFormsInfo(){
        User logUser = userService.getLogUsr();
        List<WorkFlow> wkfls = wfs.findAplt(WorkFlow.TRANS_PERSON,logUser);
        List<WorkFlowVo> wfvos = wfs.getVoFromWfs(wkfls);
        return wfvos;
    }

    @RequestMapping("/getOneFormHtml")
    @ResponseBody
    public ModelAndView getOneFormHtml(String id, HttpServletRequest request, HttpServletResponse response){
        System.out.println(id);
        request.getSession().removeAttribute("chosed_wfid");
        request.getSession().setAttribute("chosed_wfid",id);
        ModelAndView mv = new ModelAndView(viewName+"transper_form");
        return mv;
    }

    /**
     * 人员借调附件上传
     */
    @RequestMapping(value = "/upTransPerAtta", method = RequestMethod.POST)
    @ResponseBody
    public ResultVo upload(MultipartFile files, HttpServletRequest request) {
        String path = settings.getAttachmentPath();
        FileResultVo frv = attachmentService.save(files, path);
        return frv;
    }


    /**
     * 提交
     */
    @RequestMapping("/addAndCom")
    public void addAndCom(AddForm addForm, String bczl, String[] ids,HttpServletRequest request, HttpServletResponse response) throws IOException {
        String wfid = (String)request.getSession().getAttribute("chosed_wfid");
        try {
            tpService.add(addForm, wfid, ids, bczl);
            MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(true, "保存成功").build());
        } catch (Exception e) {
            MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, "保存失败").build());
        }
    }

    @RequestMapping("/commitSaved")
    public void commitSaved(String id){
        tpService.commitSaved(id);
    }


    @RequestMapping("/deleteWtj")
    public void deleteWtj(String id,HttpServletRequest request, HttpServletResponse response) throws IOException{
        try {
            tpService.deleteWtj(id);
            MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(true, "删除成功").build());
        } catch (Exception e) {
            MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, "删除失败").build());
        }
    }


//    @RequestMapping("/getApplyRecords")
//    @ResponseBody
//    public ModelAndView getApplyRecords(){
//        ModelAndView mv = new ModelAndView(viewName+"t_apply_records");
//        return mv;
//    }

    /** 由于前端大多是直接拷贝，所以state在这里做一下判断
     * @param state
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/getApplyTables")
    @ResponseBody
    public ModelAndView getApplyTables(@RequestParam(name = "lx", required = false)String state,
                                       HttpServletResponse response) throws IOException{
        ModelAndView mv = new ModelAndView(viewName+"t_apply_records");
        User user = userService.getLogUsr();
        List<TransAplyRecds> transAplyRecds = tpService.getApplyRecs(user,state);
        mv.addObject("transAplyRecds",transAplyRecds);
        return mv;
    }


    /**
     *
     * @param state waiting和passed和rejected
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/getApplyTablesContent")
    public void getApplyTablesContent(@RequestParam(name = "lx", required = false)String state,
                                       HttpServletResponse response) throws IOException{
        User user = userService.getLogUsr();
        List<TransAplyRecds> transAplyRecds = tpService.getApplyRecs(user,state);
        MyResponseBuilder.writeJsonResponse(response,
                JsonResult.useDefault(true, "获取申请记录成功", transAplyRecds).build());
    }



    @RequestMapping("/sqjlxqE")
    @ResponseBody
    public ModelAndView sqjlxqE(@RequestParam(name = "id", required = true)String id) {
        ModelAndView modelAndView = new ModelAndView(viewName + "transper_form_detail");
        TransferPersonnel tp = tpService.findById(id);
        TransperDetail tpDetail = tpService.getDetailFromTp(tp);
        modelAndView.addObject("tpDetail", tpDetail);
        return modelAndView;
    }


    @RequestMapping("/sqjlxqNE")
    public ModelAndView sqjlxqNE(@RequestParam(name = "id", required = true)String id) {
        ModelAndView modelAndView = new ModelAndView(viewName + "t_apply_recordDetail_ne");
        TransferPersonnel tp = tpService.findById(id);
        TransperDetail tpDetail = tpService.getDetailFromTp(tp);
        modelAndView.addObject("tpDetail", tpDetail);
        return modelAndView;
    }

//    /**
//     * 获取审批记录的页面
//     * @return
//     */
//    @RequestMapping("/getCheckRecords")
//    @ResponseBody
//    public ModelAndView getCheckRecords(){
//        ModelAndView mv = new ModelAndView(viewName+"t_check_records");
//        return mv;
//    }

    @GetMapping(value = "/transpersp")
    public ModelAndView transpersp(@RequestParam(name = "lx", required = false)String lx) {
        User user = userService.getLogUsr();
        if(StringUtil.isBlank(lx)) lx = "dsp";
        ModelAndView modelAndView = new ModelAndView(viewName + "t_approve_records");
        List<TransferPersonnelVo> transferPersonnelVos = tpService.getCheckForms(user);
        modelAndView.addObject("transferPersonnelVos",transferPersonnelVos);
        return modelAndView;
    }


    @GetMapping(value = "/transperspContent")
    public void transperspContent(@RequestParam(name = "lx", required = false)String lx,HttpServletResponse response) throws IOException{
        User user = userService.getLogUsr();
        List<TransferPersonnelVo> transferPersonnelVos;
        if(StringUtil.isBlank(lx) || lx.equals("dsp")) {
            lx = "dsp";
            transferPersonnelVos = tpService.getCheckForms(user);
        } else {
            // 找到该节点所有人员审批过的申请
            transferPersonnelVos = tpService.getCheckedForms(user);
        }
        MyResponseBuilder.writeJsonResponse(response,
                JsonResult.useDefault(true, "获取已审批记录成功", transferPersonnelVos).build());
    }

    /**
     * 获取登录用户所有需要审批的表单
     */
    @RequestMapping("/getCheckForms")
    public List<TransferPersonnelVo> getCheckForms(){
        User user = userService.getLogUsr();
        return tpService.getCheckForms(user);
    }

    /**
     * 根据id获取用户选中的审核表格,这里需要返回的是表单信息和已经审核的记录信息
     * @param id transper的id
     */
    @RequestMapping("/getOneCheck")
    @ResponseBody
    public ModelAndView getOneCheck(String id){
        ModelAndView modelAndView = new ModelAndView(viewName + "t_approve_detail");
        TransferPersonnel tp = tpService.findById(id);
        TransperDetail tpDetail = tpService.getDetailFromTp(tp);
        modelAndView.addObject("tpDetail", tpDetail);
        return modelAndView;
    }

    /**
     * 流程审批
     * @param response
     * @param cljg
     * @param message
     * @param clnrid
     * @throws IOException
     */
    @RequestMapping(value = "/commitCheck", method = RequestMethod.POST)
    public void addLcrz(HttpServletResponse response,
                        @RequestParam(name = "cljg", required = true)String cljg,
                        @RequestParam(name = "yj", required = false) String message,
                        @RequestParam(name = "id", required = true)  String clnrid) throws IOException{
        if(message == null) message  ="";
        try{
            tpService.checkApply(clnrid,message,cljg);
            MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(true, "借调审核成功！").build());
        }catch(Exception e) {
            MyResponseBuilder.writeJsonResponse(response, JsonResult.useDefault(false, "借调审核失败！").build());
        }
    }


    /**
     * 获取打印界面的信息
     * @param id
     * @return
     */
    @RequestMapping("/getPrintHtml")
    public ModelAndView getPrintHtml(@RequestParam(name = "id", required = true)String id) {
        ModelAndView modelAndView = new ModelAndView(viewName + "t_apply_print");
        TransferPersonnel transferPersonnel = tpService.findById(id);
        TransPrint transPrint = tpService.getTransPrintFromTransP(transferPersonnel);
        modelAndView.addObject("transPrint", transPrint);
        return modelAndView;
    }

    @GetMapping("/transPerPrint")
    public void transPerPrint(HttpServletRequest request, String xgdm, String xgid, String birthday, HttpServletResponse response) {
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
        TransPerFactory factory = null;
        String factoryBeanName = "TransPerFactory";
        try{
            factory = (TransPerFactory) context.getBean(factoryBeanName);//获取本次转换的factory，目前只有两个可以选
        }catch(Exception e) {
            logger.error("未找到word生成factory："+factoryBeanName, e);
        }

        byte[] content = null;

        try {
            String fileName = factory.getFileName(xgid);//设置最后生成的word文件名
            content = factory.create(xgid,birthday);//利用wordxml的create函数创建word
            WordXmlUtil.sendToResponse(response, content, fileName);//将转换后的word发送给用户
        } catch (Exception e) {
            logger.error("表格生成失败,xgdm " + xgdm +"xgid "+ xgid, e);
        }
    }


}
